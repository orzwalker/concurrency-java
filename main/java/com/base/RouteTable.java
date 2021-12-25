package com.base;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 通过{@link java.util.concurrent.CopyOnWriteArrayList}实现路由表
 *
 * @author walker
 * @since 2021/12/25 17:57
 */
public class RouteTable {

    private ConcurrentMap<String, CopyOnWriteArraySet<Route>> routeTable = new ConcurrentHashMap<>();

    public void addRoute(Route route) {
        routeTable.computeIfAbsent(route.systemCode, v -> new CopyOnWriteArraySet<>()).add(route);
    }

    public Set<Route> getRoute(String key) {
        return routeTable.get(key);
    }

    public void remove(Route route) {
        CopyOnWriteArraySet<Route> sets = routeTable.get(route.systemCode);
        if (null != sets && !sets.isEmpty()) {
            sets.remove(route);
        }
    }


    /**
     * 具体路由
     */
    public static class Route {
        /**
         * 服务名
         */
        private String systemCode;

        /**
         * 服务端ip
         */
        private String ip;

        /**
         * 服务端口号
         */
        private Integer port;

        public Route(String systemCode, String ip, Integer port) {
            this.systemCode = systemCode;
            this.ip = ip;
            this.port = port;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o instanceof Route) {
                Route route = (Route) o;
                return Objects.equals(systemCode, route.systemCode)
                        && Objects.equals(ip, route.ip)
                        && Objects.equals(port, route.port);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(systemCode, ip, port);
        }
    }

    /**
     * 测试
     */
    public static void main(String[] args) {
        RouteTable routeTable = new RouteTable();
        Route user = new Route("user", "192.168.1.10", 8000);
        Route user1 = new Route("user", "192.168.1.11", 8000);
        Route user2 = new Route("user", "192.168.1.12", 8000);
        Route doctor = new Route("doctor", "192.168.1.20", 8000);
        Route doctor1 = new Route("doctor", "192.168.1.21", 8000);

        routeTable.addRoute(user);
        routeTable.addRoute(user1);
        routeTable.addRoute(user2);
        routeTable.addRoute(doctor);
        routeTable.addRoute(doctor1);

        Set<Route> userRoutes = routeTable.getRoute("user");
        Set<Route> doctorRoutes = routeTable.getRoute("doctor");
        System.out.println(userRoutes.size());
        System.out.println(doctorRoutes.size());
        System.out.println("=================");

        routeTable.remove(user2);
        System.out.println(userRoutes.size());
    }
}
