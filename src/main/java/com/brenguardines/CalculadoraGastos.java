package com.brenguardines;

import com.brenguardines.exceptions.PresupuestoInsuficienteException;
import com.brenguardines.model.Gasto;
import com.brenguardines.model.Ingreso;
import com.brenguardines.model.Movimiento;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class CalculadoraGastos {

  //Ahora se utiliza ArrayList en vez de arrays
  private ArrayList<Movimiento> todosLosMovimientos;
  private HashMap<String, Gasto> gastosMap; //Para buscar rapido

  private double presupuesto;
  private Scanner scanner;

  public CalculadoraGastos() {
    todosLosMovimientos = new ArrayList<>();
    gastosMap = new HashMap<>();
    presupuesto = 0.0;
    scanner = new Scanner(System.in);
  }

  public void empezar() {
    System.out.println("\n=========================================");
    System.out.println("   CALCULADORA DE GASTOS PERSONALES");
    System.out.println("=========================================");

    int opcion;

    do {
      mostrarMenu();
      opcion = leerOpcion();

      //Try-catch para manejar errores
      try {
        switch (opcion) {
          case 1:
            agregarGasto();
            break;
          case 2:
            agregarIngreso();
            break;
          case 3:
            verTodo();
            break;
          case 4:
            verSoloGastos();
            break;
          case 5:
            verPorCategoria();
            break;
          case 6:
            buscarGasto();
            break;
          case 7:
            verResumen();
            break;
          case 8:
            System.out.println("\nGracias por usar la calculadora!");
            break;
          default:
            System.out.println("Opcion no valida");
        }
      } catch (PresupuestoInsuficienteException e) {
        //Manejo de exception
        System.out.println("ERROR: " + e.getMessage());
      }
    } while (opcion != 8);

    scanner.close();
  }

  private void mostrarMenu() {
    System.out.println("\n--- MENU PRINCIPAL ---");
    System.out.println("1. Registrar un gasto");
    System.out.println("2. Registrar un ingreso");
    System.out.println("3. Ver todo");
    System.out.println("4. Ver solo gastos");
    System.out.println("5. Ver solo por categoria");
    System.out.println("6. Buscar un gasto");
    System.out.println("7. Ver resumen");
    System.out.println("8. Salir");
    System.out.println("Presupuesto disponible: $" + presupuesto);
    System.out.print("Elige una opcion: ");
  }

  private int leerOpcion() {
    int opcion = 0;

    while (opcion < 1 || opcion > 8) {
      if (scanner.hasNextInt()) {
        opcion = scanner.nextInt();
        if (opcion < 1 || opcion > 8) {
          System.out.print("Debe ser entre 1 y 8, intenta de nuevo: ");
        }
      } else {
        System.out.println("Eso no es un numero, intenta de nuevo: ");
        scanner.next();
      }
    }

    return opcion;
  }

  private void agregarGasto() throws PresupuestoInsuficienteException{
    scanner.nextLine();

    System.out.println("\n--- AGREGAR GASTO ---");
    System.out.print("¿Que compraste/pagaste?: ");
    String descripcion = scanner.nextLine();

    //Validar que escribio algo
    while (descripcion.trim().isEmpty()) {
      System.out.println("Necesitas escribir algo, en que gastaste?: ");
      descripcion = scanner.nextLine();
    }

    //Leer cuanto gasto
    System.out.print("¿Cuanto gastaste? $");
    double monto = leerMonto();

    //EXCEPTION validar presupuesto antes de agregar
    if (monto > presupuesto) {
      throw new PresupuestoInsuficienteException(monto - presupuesto);
    }

    //Elegir categoria
    System.out.println("Categorias:");
    System.out.println("1. Comida");
    System.out.println("2. Transporte");
    System.out.println("3. Entretenimiento");
    System.out.println("4. Salud");
    System.out.println("5. Otros");
    System.out.print("¿En que categoria va? ");

    int cat = leerCategoriaValida();
    String categoria = "";

    if (cat == 1) {
      categoria = "Comida";
    } else if (cat == 2) {
      categoria = "Transporte";
    } else if (cat == 3) {
      categoria = "Entretenimiento";
    } else if (cat == 4) {
      categoria = "Salud";
    } else {
      categoria = "Otros";
    }

    //Creo el objeto Gasto
    Gasto nuevoGasto = new Gasto(descripcion, monto, categoria);

    //Lo agrego al ArrayList
    todosLosMovimientos.add(nuevoGasto);

    //Tambien lo agrego al HashMap para buscarlo despues
    gastosMap.put(descripcion.toLowerCase(), nuevoGasto);

    presupuesto -= monto;

    System.out.println("\nListo! Gasto registrado");
    System.out.println("Gastaste: $" + monto);
    System.out.println("Te quedan: $" + presupuesto);

    //Avisar si ya se paso
    if (presupuesto < 0) {
      System.out.println("CUIDADO! Ya te pasaste de tu presupuesto! Estas en Negativo!");
    }
  }

  private void agregarIngreso() {
    scanner.nextLine();

    System.out.println("\n--- AGREGAR INGRESO ---");
    System.out.print("De donde vino la plata?: ");
    String descripcion = scanner.nextLine();

    //Validar que escribio algo
    while (descripcion.trim().isEmpty()) {
      System.out.println("Necesitas escribir algo: ");
      descripcion = scanner.nextLine();
    }

    //Leer cuanto ingreso
    System.out.print("¿Cuanto ingresaste? $");
    double monto = leerMonto();

    //Creo el objeto Ingreso
    Ingreso nuevoIngreso = new Ingreso(descripcion, monto);

    //Lo agrego al ArrayList
    todosLosMovimientos.add(nuevoIngreso);

    presupuesto += monto;

    System.out.println("\nListo! Ingreso registrado");
    System.out.println("Ingresaste: $" + monto);
    System.out.println("Ahora tenes: $" + presupuesto);
  }

  private void verTodo() {
    System.out.println("\n--- TODOS LOS MOVIMIENTOS ---");

    if (todosLosMovimientos.isEmpty()) {
      System.out.println("No hay nada registrado todavia");
      return;
    }

    //Aca aparece el polimorfismo: cada uno se muestra diferente
    for (Movimiento m : todosLosMovimientos) {
      m.mostrar();
    }

    System.out.println("\nTotal: " + todosLosMovimientos.size() + " movimientos");
  }

  private void verSoloGastos() {
    System.out.println("\n--- MIS GASTOS ---");

    int contador = 0;

    //Filtro solo los gastos usando instanceof
    for (Movimiento m : todosLosMovimientos) {
      if (m instanceof Gasto) {
        m.mostrar();
        contador++;
      }
    }

    if (contador == 0) {
      System.out.println("No hay gastos todavia");
    } else {
      System.out.println("\nTotal: " + contador + " gastos");
    }
  }

  private void verPorCategoria() {
    if (todosLosMovimientos.isEmpty()) {
      System.out.println("Todavia no has registrado gastos");
      return;
    }

    System.out.println("\n--- VER POR CATEGORIA --- :");
    System.out.println("1. Comida");
    System.out.println("2. Transporte");
    System.out.println("3. Entretenimiento");
    System.out.println("4. Salud");
    System.out.println("5. Otros");
    System.out.print("¿Que categoria queres ver? ");

    int cat = leerCategoriaValida();
    String categoriaElegida = "";

    if (cat == 1) {
      categoriaElegida = "Comida";
    } else if (cat == 2) {
      categoriaElegida = "Transporte";
    } else if (cat == 3) {
      categoriaElegida = "Entretenimiento";
    } else if (cat == 4) {
      categoriaElegida = "Salud";
    } else {
      categoriaElegida = "Otros";
    }

    System.out.println("\nGastos en: " + categoriaElegida);

    double totalCat = 0;
    int contador = 0;

    for (Movimiento m : todosLosMovimientos) {
      if (m instanceof Gasto) {
        Gasto g = (Gasto) m;
        if (g.getCategoria().equals(categoriaElegida)) {
          contador++;
          System.out.println(contador + ". " + g.getDescripcion() + " - $" + g.getMonto());
          totalCat += g.getMonto();
        }
      }
    }

    if (contador == 0) {
      System.out.println("No tienes gastos en esta categoria");
    } else {
      System.out.println("\nTotal en " + categoriaElegida + ": $" + totalCat);
      String texto = (contador == 1) ? " gasto" : " gastos";
      System.out.println("Tienes " + contador + texto + " en esta categoria");
    }

  }

  private int leerCategoriaValida() {
    int opcion = 0;

    while (opcion < 1 || opcion > 5) {
      if (scanner.hasNextInt()) {
        opcion = scanner.nextInt();
        if (opcion < 1 || opcion > 5) {
          System.out.println("Debe ser entre 1 y 5: ");
        }
      } else {
        System.out.println("Eso no es un numero: ");
        scanner.next();
      }
    }

    return opcion;
  }

  private void buscarGasto() {
    scanner.nextLine();

    System.out.println("\n--- BUSCAR GASTO ---");
    System.out.print("Que gasto queres buscar?: ");
    String buscar = scanner.nextLine().toLowerCase();

    //Aca uso el HashMap para buscar rapido
    Gasto encontrado = gastosMap.get(buscar);

    if (encontrado != null) {
      System.out.println("\nEncontrado:");
      encontrado.mostrar();
    } else {
      System.out.println("No encontre ese gasto");
    }
  }

  private void verResumen() {
    System.out.println("\n==========================================");
    System.out.println("           RESUMEN FINANCIERO             ");
    System.out.println("==========================================");

    if (todosLosMovimientos.isEmpty()) {
      System.out.println("No hay gastos para mostrar");
      return;
    }

    //Calcular todo
    double totalGastos = 0;
    double totalIngresos = 0;
    int cantGastos = 0;
    int cantIngresos = 0;

    //Separo gastos de ingresos
    for (Movimiento m : todosLosMovimientos) {
      if (m instanceof Gasto) {
        totalGastos += m.getMonto();
        cantGastos++;
      } else if (m instanceof Ingreso) {
        totalIngresos += m.getMonto();
        cantIngresos++;
      }
    }

    System.out.println("\nIngresos: $" + totalIngresos + " (" + cantIngresos + ")");
    System.out.println("Gastos: $" + totalGastos + " (" + cantGastos + ") ");
    System.out.println("Balance: $" + (totalIngresos - totalGastos));
    System.out.println("Disponible ahora: $" + presupuesto);

    if (cantGastos > 0) {
      double promedio = totalGastos / cantGastos;
      System.out.println("Promedio por gasto: $" + promedio);
    }

    //Mostrar por categoria
    System.out.println("Por categoria: ");
    mostrarTotalesPorCategoria();
  }

  private void mostrarTotalesPorCategoria() {
    String[] categorias = {"Comida", "Transporte", "Entretenimiento", "Salud", "Otros"};

    for (String cat : categorias) {
      double totalCat = 0;
      int cantidad = 0;

      for (Movimiento m : todosLosMovimientos) {
        if (m instanceof Gasto) {
          Gasto g = (Gasto) m;
          if (g.getCategoria().equals(cat)) {
            totalCat += g.getMonto();
            cantidad++;
          }
        }
      }

      if (cantidad > 0) {
        String texto = (cantidad == 1) ? " gasto" : " gastos";
        System.out.println(" " + cat + ": $" + totalCat + " (" + cantidad + texto + ")");
      }
    }
  }

  private double leerMonto() {
    double monto = -1;

    while (monto <= 0) {
      if (scanner.hasNextDouble()) {
        monto = scanner.nextDouble();
        if (monto <= 0) {
          System.out.println("Debe ser mayor a cero. Intenta de nuevo: $");
        }
      } else {
        System.out.println("Eso no es un numero valido. Intetna de nuevo: $");
        scanner.next();
      }
    }

    return monto;
  }

  public static void main(String[] args) {
    CalculadoraGastos calculadoraGastos = new CalculadoraGastos();
    calculadoraGastos.empezar();
  }
}