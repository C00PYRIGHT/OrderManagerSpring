package rendeleskezelo.service;

import rendeleskezelo.model.LitophaneOrder;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    /**
     * Új rendelés hozzáadása.
     *
     * @param litophaneOrder A menteni kívánt rendelés.
     * @return A mentett rendelés.
     */
    LitophaneOrder addOrder(LitophaneOrder litophaneOrder);

    /**
     * Rendelés módosítása.
     *
     * @param litophaneOrder A frissíteni kívánt rendelés adatai.
     * @return A frissített rendelés.
     */
    LitophaneOrder updateOrder(LitophaneOrder litophaneOrder);

    /**
     * Rendelés törlése azonosító alapján.
     *
     * @param id A törlendő rendelés azonosítója.
     */
    void deleteOrder(Long id);

    /**
     * Egy rendelés lekérdezése azonosító alapján.
     *
     * @param id A keresett rendelés azonosítója.
     * @return A megtalált rendelés, ha létezik.
     */
    LitophaneOrder findOrderById(Long id);

    /**
     * Az összes rendelés lekérdezése.
     *
     * @return Az összes rendelés listája.
     */
    List<LitophaneOrder> findAllOrders();

    /**
     * Rendelések lekérdezése ügyfél alapján.
     *
     * @param customerId Az ügyfél azonosítója.
     * @return Az adott ügyfélhez tartozó rendelések.
     */
    List<LitophaneOrder> findOrdersByCustomerId(Long customerId);

    /**
     * Rendelések lekérdezése státusz alapján.
     *
     * @param statusId A státusz azonosítója.
     * @return Az adott státuszhoz tartozó rendelések.
     */
    List<LitophaneOrder> findOrdersByStatusId(Long statusId);

    List<LitophaneOrder> getAllOrders();
    List<LitophaneOrder> findOrdersWithDeadlineWithinAWeek();
    int sumPrice();
    int sumProfit();
    int sumOut();
}
