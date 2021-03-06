package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  private double saldo; //No es neceserio definir el default ya que se hace en el constructor
  private List<Movimiento> movimientos = new ArrayList<>();

  public Cuenta() {
    saldo = 0;
  }

  public Cuenta(double montoInicial) {
    saldo = montoInicial;
  }

  public void setMovimientos(List<Movimiento> movimientos) {
    this.movimientos = movimientos;
  } //No tendria sentidopoder settear los movimientos sin modificar el saldo de la cuenta

  public void poner(double cantidadDinero) { //Nombre de la variable "cuanto" es poco declarativo, seria mejor "cantidadDinero"
    if (cantidadDinero <= 0) {
      throw new MontoNegativoException(cantidadDinero + ": el monto a ingresar debe ser un valor positivo");
    }

    if (getMovimientos().stream().filter(movimiento -> movimiento.isDeposito()).count() >= 3) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }
    this.saldo += cantidadDinero;//Falta modificar el saldo de la cuenta
    this.agregarMovimiento(LocalDate.now(), cantidadDinero, true); // Usar funcion agregarMovimiento en vez de esto
  }

  public void sacar(double cantidadDinero) {//Nombre de la variable "cuanto" es poco declarativo, seria mejor "cantidadDinero"
    if (cantidadDinero <= 0) {
      throw new MontoNegativoException(cantidadDinero + ": el monto a ingresar debe ser un valor positivo");
    }
    if (getSaldo() - cantidadDinero < 0) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }
    double montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    double limite = 1000 - montoExtraidoHoy;
    if (cantidadDinero > limite) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + 1000
          + " diarios, l??mite: " + limite);
    }
    this.saldo -= cantidadDinero;//Falta modificar el saldo de la cuenta
    this.agregarMovimiento(LocalDate.now(), cantidadDinero, false); // Usar funcion agregarMovimiento en vez de esto
  }

  public void agregarMovimiento(LocalDate fecha, double cuanto, boolean esDeposito) {
    Movimiento movimiento = new Movimiento(fecha, cuanto, esDeposito);
    movimientos.add(movimiento);
  }

  public double getMontoExtraidoA(LocalDate fecha) {
    return getMovimientos().stream()
        .filter(movimiento -> movimiento.fueExtraido(fecha)) //Usar metodo del movimiento("fueExtraido")
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public double getSaldo() {
    return saldo;
  }

  public void setSaldo(double saldo) {
    this.saldo = saldo;
  } //No se si estaria bien poder settear el saldo, quizas se puede hacer mediante el metodo poner

}
