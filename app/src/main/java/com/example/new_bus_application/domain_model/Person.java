package com.example.new_bus_application.domain_model;

import com.example.new_bus_application.domain_model.DAO.BusDAOAndroid;
import com.example.new_bus_application.domain_model.DAO.StationDAOAndroid;


import java.util.ArrayList;
import java.util.HashMap;

public class Person {
    private static String[] message = new String[1];
    private static Station chosen_station;
    private static Route chosen_route;
    private static Bus chosen_bus;

    public static String getMessage() {
        return message[0];
    }

    public static Station getChosen_station() {
        return chosen_station;
    }

    public static Route getChosen_route() {
        return chosen_route;
    }

    public static Bus getChosen_bus() {
        return chosen_bus;
    }

    public static void setMessage(String message) {
        Person.message[0] = message;
    }

    public static void setChosen_station(Station chosen_station) {
        Person.chosen_station = chosen_station;
    }

    public static void setChosen_route(Route chosen_route) {
        Person.chosen_route = chosen_route;
    }

    public static void setChosen_bus(Bus chosen_bus) {
        Person.chosen_bus = chosen_bus;
    }

    public static void connect(Bus bus, Route route) {
        bus.addRoute(route);
        route.setBus(bus);
    }

    public static void connect(Route route, Station station) {
        route.addStation(station);
        station.addRoute(route);
    }

    public static ArrayList<Station> getStations(String bus_name) {
        HashMap<String, Bus> buses = BusDAOAndroid.getBuses();
        Bus bus = buses.get(bus_name);
        ArrayList<Station> stations = new ArrayList<>();
        for (Route route : bus.getRoutes())
            for (Station station : route.getStations())
                if (!stations.contains(station))
                    stations.add(station);
        return stations;
    }

    public static ArrayList<Bus> getBuses(String station_name) {
        HashMap<String, Station> stations = StationDAOAndroid.getStations();
        Station station = stations.get(station_name);
        ArrayList<Bus> buses = new ArrayList<>();
        for (Route route : station.getRoutes())
            if (!buses.contains(route.getBus()))
                buses.add(route.getBus());
        return buses;
    }

    public static ArrayList<Station> getNearestStations(double latitude, double longitude) {
        ArrayList<Station> nearestStations = new ArrayList<>(3);
        ArrayList<Station> stations = StationDAOAndroid.ListStations();
        if(stations.size()<=3) return stations;
        for (int i = 0; i < 3; i++) {
            int nearestStation_index=-1;
            double distance=-1;
            for(int j=0; j<stations.size(); j++){
                if(!nearestStations.contains(stations.get(j))){
                    nearestStation_index = 0;
                    distance = Math.sqrt(Math.pow(stations.get(0).getLongitude() - longitude, 2) +
                            Math.pow(stations.get(0).getLatitude() - latitude, 2));
                }

            }

            for (int j = 1; j < stations.size(); j++) {
                double next_distance = Math.sqrt(Math.pow(stations.get(j).getLongitude() - longitude, 2) +
                        Math.pow(stations.get(j).getLatitude() - latitude, 2));
                if (!nearestStations.contains(stations.get(j)) && distance > next_distance) {
                    distance = next_distance;
                    nearestStation_index = j;
                }
            }
            nearestStations.add(stations.get(nearestStation_index));
        }
        return nearestStations;
    }

    private static boolean findRoute(String Start, String End, ArrayList<Route> t_routes, ArrayList<Station> t_stations,
                                    String[] message_, int ammount) {
        ArrayList<Route> Start_routes = new ArrayList<>();
        Station Start_station = StationDAOAndroid.getStations().get(Start);
        ArrayList<Route> End_routes = new ArrayList<>();
        Station End_station = StationDAOAndroid.getStations().get(End);
        Start_routes.addAll(Start_station.getRoutes());
        End_routes.addAll(End_station.getRoutes());
        for (Route route : subRoutes(Start_routes, t_routes)) {
            if (End_routes.contains(route)&&route.getStations().indexOf(Start_station)<route.getStations().indexOf(End_station)) {
                message_[0] += "Go to The bus: " + route.getBus().getName() + " with route: " + route.getName()
                        + " embark at station: " + Start + " and disembark at station: " + End + "\n";
                return true;
            }
        }
        if (ammount <= 1) return false;
        t_stations.add(Start_station);
        ArrayList<Route> subbed_routes= subRoutes(Start_routes, t_routes);
        for (Route route :subbed_routes) {
            ArrayList<Station> subbed_stations=subStation(route.getStations(), t_stations,Start_station);
            for (Station station : subbed_stations) {
                ArrayList<Station> parameter_t_stations = new ArrayList<>();
                parameter_t_stations.addAll(t_stations);
                String[] message_parameter = {message_[0] + "Go to The bus: " + route.getBus().getName() +
                        " with route: " + route.getName() +
                        " embark at station: " + Start + " and disembark at station: "
                        + station.getName() + "\n"};
                if (findRoute(station.getName(), End, addRoute(t_routes, route), parameter_t_stations,
                        message_parameter, ammount - 1)) {
                    message_[0] = message_parameter[0];
                    return true;
                }

            }
        }

        return false;
    }

    public static boolean checkRoutes(String Start, String End) {
        for (int ammount = 1; ammount <= 2*BusDAOAndroid.getBuses().size(); ammount++) {
            message[0] = "";
            if (findRoute(Start, End, new ArrayList<Route>(), new ArrayList<Station>(), message, ammount))
                return true;
        }
        message[0]="There are no buses";
        return false;
    }

    private static ArrayList<Station> subStation(ArrayList<Station> stations, ArrayList<Station> t_stations,Station begin) {
        ArrayList<Station> returned = new ArrayList<Station>();
        boolean add=false;
        for(Station station:stations) {
            if (station == begin) {
                add = true;
                continue;
            }
            if(add)
                returned.add(station);
        }
        returned.removeAll(t_stations);

        return returned;
    }

    private static ArrayList<Route> subRoutes(ArrayList<Route> routes, ArrayList<Route> t_routes) {
        ArrayList<Route> returned = new ArrayList<>();
        returned.addAll(routes);
        returned.removeAll(t_routes);
        return returned;
    }

    private static ArrayList<Route> addRoute(ArrayList<Route> routes, Route new_route) {
        ArrayList<Route> returned = new ArrayList<>();
        returned.addAll(routes);
        returned.add(new_route);
        return returned;
    }
}