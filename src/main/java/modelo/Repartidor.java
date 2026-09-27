package modelo;

import java.util.ArrayList;

public class Repartidor implements Runnable {

    private String nombre;
    private ZonaDeCarga zonaDeCarga;
    private ArrayList<Pedido> pedidosAsignados;
    private int id;

    public Repartidor(String nombre, ZonaDeCarga zonaDeCarga) {
        this.nombre = nombre;
        this.zonaDeCarga = zonaDeCarga;
        this.pedidosAsignados = new ArrayList<>();
    }

    public Repartidor(int id, String nombre) {
        this(nombre, null);
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Pedido> getPedidosAsignados() {
        return pedidosAsignados;
    }

    @Override
    public void run() {
        if (zonaDeCarga == null) {
            return;
        }

        while (true) {

            Pedido pedido = zonaDeCarga.retirarPedido();

            if (pedido == null) {
                break;
            }

            pedidosAsignados.add(pedido);

            System.out.println(
                    "[Repartidor - " + nombre
                            + "] Retirando pedido #"
                            + pedido.getIdPedido() + "..."
            );

            pedido.asignarRepartidor(nombre);
            pedido.setEstado("EN_REPARTO");

            System.out.println(
                    "[Repartidor - " + nombre
                            + "] Estado: "
                            + pedido.getEstado()
            );

            System.out.println(
                    "[Repartidor - " + nombre
                            + "] Entregando pedido #"
                            + pedido.getIdPedido() + "..."
            );

            try {

                int tiempo = 1000 + (int) (Math.random() * 2000);

                Thread.sleep(tiempo);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
                return;
            }

            pedido.setEstado("ENTREGADO");

            System.out.println(
                    "[Repartidor - " + nombre
                            + "] Estado: "
                            + pedido.getEstado()
            );

            System.out.println();
        }
    }
}