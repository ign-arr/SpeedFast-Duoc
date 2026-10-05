package modelo;

import interfaces.Cancelable;
import interfaces.Despachable;

// Clase abstracta
public abstract class Pedido implements Despachable, Cancelable {

    private int idPedido;
    private String direccionEntrega;
    private int distanciaKm;
    private volatile EstadoPedido estado;
    private volatile String nombreRepartidor;

    public Pedido(int idPedido, String direccionEntrega, int distanciaKm) {
        this.idPedido = idPedido;
        this.direccionEntrega = direccionEntrega;
        this.distanciaKm = distanciaKm;
        this.estado = EstadoPedido.PENDIENTE;
        this.nombreRepartidor = "Sin asignar";
    }

    public String getTipo() {
        if (this instanceof PedidoComida) return "COMIDA";
        if (this instanceof PedidoEncomienda) return "ENCOMIENDA";
        return "EXPRESS";
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }

    public int getDistanciaKm() {
        return distanciaKm;
    }

    public void setDistanciaKm(int distanciaKm) {
        this.distanciaKm = distanciaKm;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(String nuevoEstado) {
        this.estado = EstadoPedido.valueOf(nuevoEstado);
    }

    public String getNombreRepartidor() {
        return nombreRepartidor;
    }

    public void setNombreRepartidor(String nombreRepartidor) {
        this.nombreRepartidor = nombreRepartidor;
    }

    public void mostrarResumen() {
        System.out.println("Pedido #" + idPedido);
        System.out.println("Dirección: " + direccionEntrega);
        System.out.println("Distancia: " + distanciaKm + " km");
        System.out.println("Estado: " + estado);
    }

    public void asignarRepartidor() {
        System.out.println("Asignando repartidor...");
    }

    public void asignarRepartidor(String nombreRepartidor) {
        this.nombreRepartidor = nombreRepartidor;

        System.out.println(
                "→ Pedido asignado a " + nombreRepartidor
        );
    }

    @Override
    public void despachar() {
        System.out.println("Pedido despachado correctamente.");
    }

    @Override
    public void cancelar() {
        System.out.println("→ Pedido cancelado exitosamente.");
    }

    public abstract int calcularTiempoEntrega();

    @Override
    public String toString() {
        return "Pedido{" +
                "idPedido=" + idPedido +
                ", direccionEntrega='" + direccionEntrega + '\'' +
                ", distanciaKm=" + distanciaKm +
                ", estado=" + estado +
                '}';
    }
}