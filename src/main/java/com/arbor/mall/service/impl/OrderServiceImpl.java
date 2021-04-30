package com.arbor.mall.service.impl;

import com.arbor.mall.common.Constant;
import com.arbor.mall.exception.ArborMallException;
import com.arbor.mall.exception.ArborMallExceptionEnum;
import com.arbor.mall.filter.UserFilter;
import com.arbor.mall.model.dao.CartMapper;
import com.arbor.mall.model.dao.OrderItemMapper;
import com.arbor.mall.model.dao.OrderMapper;
import com.arbor.mall.model.dao.ProductMapper;
import com.arbor.mall.model.pojo.Cart;
import com.arbor.mall.model.pojo.Order;
import com.arbor.mall.model.pojo.OrderItem;
import com.arbor.mall.model.pojo.Product;
import com.arbor.mall.model.request.CreateOrderReq;
import com.arbor.mall.model.vo.CartVO;
import com.arbor.mall.model.vo.OrderItemVO;
import com.arbor.mall.model.vo.OrderVO;
import com.arbor.mall.service.CartService;
import com.arbor.mall.service.OrderService;
import com.arbor.mall.service.UserService;
import com.arbor.mall.util.OrderCodeFactory;
import com.arbor.mall.util.QRCodeGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.zxing.WriterException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 描述：订单service实现类
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderMapper orderMapper;
    @Autowired
    CartMapper cartMapper;
    @Autowired
    ProductMapper productMapper;
    @Autowired
    OrderItemMapper orderItemMapper;
    @Autowired
    CartService cartService;
    @Autowired
    UserService userService;

    @Value("${file.upload.ip}")
    String ip;

    /**
     * 创建订单
     *
     * @param userId
     * @param createOrderReq
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String create(Integer userId, CreateOrderReq createOrderReq) {

        // 查找用户的购物车
        List<CartVO> cartVOS = cartService.list(userId);

        ArrayList<CartVO> cartVOListTemp = new ArrayList<>();

        // 获取已勾选状态的商品并存入新的集合中
        for (CartVO cartVO : cartVOS) {
            if (cartVO.getSelected().equals(Constant.Cart.CHECKED)) {
                cartVOListTemp.add(cartVO);
            }
        }

        // 将拿到的临时集合放入cartVOS中，过滤未被选中的商品
        cartVOS = cartVOListTemp;
        // 用户购物车中无商品，抛出异常
        if (CollectionUtils.isEmpty(cartVOS)) {
            throw new ArborMallException(ArborMallExceptionEnum.CART_EMPTY);
        }

        // 判断商品是否存在或是否库存充足
        validSaleStatusAndStock(cartVOS);

        // 将购物车中选中的商品转换为OrderItem对象
        List<OrderItem> orderItemList = cartVOListToOrderItemList(cartVOS);

        // 扣库存
        for (OrderItem orderItem : orderItemList) {
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            Integer stock = product.getStock() - orderItem.getQuantity();
            if (stock < 0) {
                throw new ArborMallException(ArborMallExceptionEnum.NOT_ENOUGH);
            }
            product.setStock(stock);
            productMapper.updateByPrimaryKeySelective(product);
        }

        // 删除已勾选的商品
        cleanCart(cartVOS);

        // 生成订单
        Order order = new Order();
        // 生成订单号
        String orderNo = OrderCodeFactory.getOrderCode(Long.valueOf(userId));
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalPrice(totalPrice(orderItemList));
        order.setReceiverName(createOrderReq.getReceiverName());
        order.setReceiverMobile(createOrderReq.getReceiverMobile());
        order.setReceiverAddress(createOrderReq.getReceiverAddress());
        order.setOrderStatus(Constant.OrderStatusEnum.NOT_PAID.getCode());
        order.setPostage(0);
        order.setPaymentType(1);
        // 将订单信息插入到表中
        orderMapper.insertSelective(order);
        // 循环保存订单中每个商品的信息
        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderNo(order.getOrderNo());
            orderItemMapper.insertSelective(orderItem);
        }

        return order.getOrderNo();
    }

    private Integer totalPrice(List<OrderItem> orderItemList) {
        // 查询订单总价
        Integer totalPrice = 0;
        for (OrderItem orderItem : orderItemList) {
            totalPrice = orderItem.getTotalPrice() + totalPrice;
        }
        return totalPrice;
    }

    private void cleanCart(List<CartVO> cartVOS) {
        // 删除已勾选的商品
        for (CartVO cartVO : cartVOS) {
            cartMapper.deleteByPrimaryKey(cartVO.getId());
        }
    }


    /**
     * 将购物车中选中的商品转换为OrderItem对象
     *
     * @param cartVOS
     * @return
     */
    private List<OrderItem> cartVOListToOrderItemList(List<CartVO> cartVOS) {
        List<OrderItem> orderItemList = new ArrayList<>();
        for (CartVO cartVO : cartVOS) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartVO.getProductId());
            // 记录商品快照信息
            orderItem.setProductName(cartVO.getProductName());
            orderItem.setProductImg(cartVO.getProductImage());
            orderItem.setUnitPrice(cartVO.getPrice());
            orderItem.setQuantity(cartVO.getQuantity());
            orderItem.setTotalPrice(cartVO.getTotalPrice());
            orderItemList.add(orderItem);
        }
        return orderItemList;
    }

    private void validSaleStatusAndStock(List<CartVO> cartVOS) {
        // 判断商品是否存在并且在售，不存在或者库存不足则抛出异常
        for (CartVO cartVO : cartVOS) {
            Product product = productMapper.selectByPrimaryKey(cartVO.getProductId());
            if (product == null || product.getStatus().equals(Constant.SaleStatus.NOT_SALE)) {
                throw new ArborMallException(ArborMallExceptionEnum.NOT_SALE);
            }
            if (cartVO.getQuantity() > product.getStock()) {
                throw new ArborMallException(ArborMallExceptionEnum.NOT_ENOUGH);
            }
        }

    }


    /**
     * 订单详情
     *
     * @param userId
     * @param orderNo
     * @return
     */
    @Override
    public OrderVO detail(Integer userId, String orderNo) {


        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new ArborMallException(ArborMallExceptionEnum.NO_ORDER);
        }
        if (!userId.equals(order.getUserId())) {
            throw new ArborMallException(ArborMallExceptionEnum.NOT_YOUR_ORDER);
        }

        OrderVO orderVO = getOrderVO(order);

        return orderVO;
    }

    private OrderVO getOrderVO(Order order) {
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        List<OrderItem> orderItemList = orderItemMapper.selectOrderItemList(order.getOrderNo());
        List<OrderItemVO> orderItemVOList = new ArrayList<>();
        for (OrderItem orderItem : orderItemList) {
            OrderItemVO orderItemVO = new OrderItemVO();
            BeanUtils.copyProperties(orderItem, orderItemVO);
            orderItemVOList.add(orderItemVO);
        }

        orderVO.setOrderItemVOList(orderItemVOList);
        orderVO.setOrderStatusName(Constant.OrderStatusEnum.codeOf(order.getOrderStatus()).getValue());

        return orderVO;
    }

    /**
     * 前台订单列表
     *
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo listForCustomer(Integer userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectByUserId(userId);
        List<OrderVO> orderVOList = getOrderVOList(orderList);
        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVOList);
        return pageInfo;
    }


    private List<OrderVO> getOrderVOList(List<Order> orderList) {
        List<OrderVO> orderVOList = new ArrayList<>();
        for (Order order : orderList) {
            OrderVO orderVO = getOrderVO(order);
            orderVOList.add(orderVO);
        }
        return orderVOList;
    }


    /**
     * 取消订单
     *
     * @param userId
     * @param orderNo
     */
    @Override
    public void cancel(Integer userId, String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new ArborMallException(ArborMallExceptionEnum.NO_ORDER);
        }
        if (!userId.equals(order.getUserId())) {
            throw new ArborMallException(ArborMallExceptionEnum.NOT_YOUR_ORDER);
        }
        if (order.getOrderStatus().equals(Constant.OrderStatusEnum.NOT_PAID.getCode())) {
            order.setOrderStatus(Constant.OrderStatusEnum.CANCELED.getCode());
            order.setEndTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new ArborMallException(ArborMallExceptionEnum.WRONG_ORDER_STATUS);
        }


    }

    /**
     * 生成支付二维码
     *
     * @param orderNo
     * @return
     */
    @Override
    public String qrcode(String orderNo) {

        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        HttpServletRequest request = attributes.getRequest();


        /*try {
            // 获取局域网ip，复杂ip获取可能不准确
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }*/


        String address = ip + ":" + request.getLocalPort();  // 获取ip和端口号
        String payUrl = "http://" + address + "/pay?orderNo=" + orderNo;
        try {
            QRCodeGenerator.generateQRCodeImage(payUrl,
                    350, 350, Constant.FILE_UPLOAD_DIR + orderNo + ".png");
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String pngAddress = "http://" + address + "/images/" + orderNo + ".png";

        return pngAddress;
    }

    /**
     * 后台订单列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectOrderList();
        List<OrderVO> orderVOList = getOrderVOList(orderList);
        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVOList);
        return pageInfo;
    }

    /**
     * 支付订单
     *
     * @param orderNo
     */
    @Override
    public void pay(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new ArborMallException(ArborMallExceptionEnum.NO_ORDER);
        }

        if (order.getOrderStatus().equals(Constant.OrderStatusEnum.NOT_PAID.getCode())) {
            order.setOrderStatus(Constant.OrderStatusEnum.PAID.getCode());
            order.setPayTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new ArborMallException(ArborMallExceptionEnum.WRONG_ORDER_STATUS);
        }

    }

    /**
     * 后台订单发货
     *
     * @param orderNo
     */
    @Override
    public void delivered(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new ArborMallException(ArborMallExceptionEnum.NO_ORDER);
        }

        if (order.getOrderStatus() .equals(Constant.OrderStatusEnum.PAID.getCode())) {
            order.setOrderStatus(Constant.OrderStatusEnum.DELIVERED.getCode());
            order.setDeliveryTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new ArborMallException(ArborMallExceptionEnum.WRONG_ORDER_STATUS);
        }


    }

    /**
     * 完结订单
     *
     * @param orderNo
     */
    @Override
    public void finish(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new ArborMallException(ArborMallExceptionEnum.NO_ORDER);
        }

        if (!userService.checkAdminRole(UserFilter.currentUser)
                && !order.getUserId().equals(UserFilter.currentUser.getId())) {
            throw new ArborMallException(ArborMallExceptionEnum.NOT_YOUR_ORDER);
        }

        if (order.getOrderStatus().equals(Constant.OrderStatusEnum.DELIVERED.getCode())) {
            order.setOrderStatus(Constant.OrderStatusEnum.FINISHED.getCode());
            order.setEndTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new ArborMallException(ArborMallExceptionEnum.WRONG_ORDER_STATUS);
        }


    }
}
