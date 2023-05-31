package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    EntityManager em;

    @DisplayName("상품 주문")
    @Test
    void 상품_주문() {
        // given
        Member member = getMember("회원1");

        Book book = getBook("JAP짱", 10000, 10);

        // when
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        Order getOrder = orderRepository.findOne(orderId);
        // then
        assertEquals(OrderStatus.ORDER, getOrder.getStatus(), "상품 주문시 상태는 ORDER");
        assertEquals(1, getOrder.getOrderItems().size(), "주문한 상품 종류 수가 정확해야 한다.");
        assertEquals(10000 * orderCount, getOrder.getTotalPrice(), "주문한 가격은 가격 * 수량이다.");
        assertEquals(8, book.getStockQuantity(), "주문 수량만큼 재고가 줄어야 한다.");
    }

    private Book getBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member getMember(String name) {
        Member member = new Member();
        member.setUsername(name);
        member.setAddress(new Address("서울", "봉천로", "49-12"));
        em.persist(member);
        return member;
    }

    @DisplayName("재고 초과")
    @Test
    void 상품_주문_재고_초과() throws  Exception {
        // given
        Member member = getMember("회원1");
        Item item = getBook("시골 JPA", 10000, 10);

        int orderCount = 11;
        // when
        // then
        assertThrows(NotEnoughStockException.class, () -> {
            orderService.order(member.getId(), item.getId(), orderCount);
        });
    }
    @DisplayName("주문 취소")
    @Test
    void 주문_취소() {
        // given
        Member member = getMember("성모");
        Book book = getBook("JPA짱", 10000, 10);

        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        // when
        orderService.cancleOrder(orderId);

        // then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.CANCLE, getOrder.getStatus(), "주문 취소 상태는 CANCLE이다.");
        assertEquals(10, book.getStockQuantity(), "주문이 취소된 상품은 그만큼 재고가 증가해야 한다.");

    }

}