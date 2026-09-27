package main;

import modelo.PedidoComida;
import modelo.PedidoEncomienda;
import modelo.PedidoExpress;
import modelo.Repartidor;
import modelo.ZonaDeCarga;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SimulacionConsola {

    public static void main(String[] args) {

        ZonaDeCarga zonaDeCarga = new ZonaDeCarga();

        zonaDeCarga.agregarPedido(
                new PedidoComida(
                        101,
                        "Pasaje 1 154",
                        4
                )
        );

        zonaDeCarga.agregarPedido(
                new PedidoExpress(
                        102,
                        "O'Higgins 102",
                        7
                )
        );

        zonaDeCarga.agregarPedido(
                new PedidoEncomienda(
                        103,
                        "Prat 21",
                        6
                )
        );

        zonaDeCarga.agregarPedido(
                new PedidoComida(
                        104,
                        "San Pedro 421",
                        3
                )
        );

        zonaDeCarga.agregarPedido(
                new PedidoExpress(
                        105,
                        "Los Pinos 32",
                        8
                )
        );

        zonaDeCarga.agregarPedido(
                new PedidoEncomienda(
                        106,
                        "Coquimbo 322",
                        5
                )
        );

        System.out.println();

        Repartidor repartidor1 = new Repartidor(
                "Pedro Arriagada",
                zonaDeCarga
        );

        Repartidor repartidor2 = new Repartidor(
                "Camila Guzmán",
                zonaDeCarga
        );

        Repartidor repartidor3 = new Repartidor(
                "Fernanda Henriquez",
                zonaDeCarga
        );

        ExecutorService executor = Executors.newFixedThreadPool(3);

        executor.execute(repartidor1);
        executor.execute(repartidor2);
        executor.execute(repartidor3);

        executor.shutdown();

        try {

            if (executor.awaitTermination(1, TimeUnit.MINUTES)) {

                System.out.println(
                        "Todos los pedidos han sido entregados correctamente"
                );

            }

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            System.out.println(
                    "El proceso fue interrumpido."
            );
        }
    }
}