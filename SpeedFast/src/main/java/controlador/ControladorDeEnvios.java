package controlador;

import interfaces.Rastreable;
import modelo.Pedido;
import modelo.EstadoPedido;
import modelo.Repartidor;
import modelo.ZonaDeCarga;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.ArrayList;

public class ControladorDeEnvios implements Rastreable {

    private ArrayList<String> historial;
    private final ArrayList<Pedido> pedidos = new ArrayList<>();
    private ExecutorService executor;

    public ControladorDeEnvios() {
        historial = new ArrayList<>();
    }

    public void agregarPedido(Pedido pedido) {
        if (pedido == null || pedido.getIdPedido() <= 0
                || pedido.getDireccionEntrega() == null
                || pedido.getDireccionEntrega().trim().isEmpty()
                || pedido.getDistanciaKm() <= 0) {
            throw new IllegalArgumentException("Completa los datos con valores válidos.");
        }
        for (Pedido existente : pedidos) {
            if (existente.getIdPedido() == pedido.getIdPedido()) {
                throw new IllegalArgumentException("Ya existe un pedido con ese ID.");
            }
        }
        pedidos.add(pedido);
    }

    public ArrayList<Pedido> getPedidos() {
        return new ArrayList<>(pedidos);
    }

    public boolean hayEntregasEnCurso() {
        return executor != null && !executor.isTerminated();
    }

    public int iniciarEntregas() {
        if (hayEntregasEnCurso()) {
            throw new IllegalStateException("Espera a que termine el reparto actual.");
        }
        ArrayList<Pedido> pendientes = new ArrayList<>();
        for (Pedido pedido : pedidos) {
            if (pedido.getEstado() == EstadoPedido.PENDIENTE) {
                pendientes.add(pedido);
            }
        }
        if (pendientes.isEmpty()) {
            return 0;
        }
        ZonaDeCarga zona = new ZonaDeCarga();
        for (Pedido pedido : pendientes) {
            zona.agregarPedido(pedido);
        }
        executor = Executors.newFixedThreadPool(3);
        executor.execute(new Repartidor("Pedro Arriagada", zona));
        executor.execute(new Repartidor("Camila Guzmán", zona));
        executor.execute(new Repartidor("Fernanda Henriquez", zona));
        executor.shutdown();
        return pendientes.size();
    }

    public void reservarPedido(Pedido pedido) {
        System.out.println(
                "Pedido #" + pedido.getIdPedido()
                        + " reservado correctamente."
        );
    }

    public void despacharPedido(Pedido pedido) {

        pedido.despachar();

        historial.add(
                pedido.getClass().getSimpleName()
                        + " #" + pedido.getIdPedido()
                        + " - entregado por "
                        + pedido.getNombreRepartidor()
        );
    }

    @Override
    public void verHistorial() {

        System.out.println("Historial:");

        for (String entrega : historial) {
            System.out.println("- " + entrega);
        }
    }
}