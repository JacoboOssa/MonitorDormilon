import java.util.concurrent.Semaphore;
import java.util.Random;

public class MonitorDormilon {
    // Número de sillas disponibles en la sala de espera
    public static int sillasLibres = 3; // Variable compartida (Zona Critica)

    public static class Monitor extends Thread {
        private Semaphore sillaDisponible; // Semáforo para controlar el acceso a la silla
        private boolean ocupado;           // Estado del Monitor, si está ocupado o no
        private Random random;            

        // Constructor de la clase. Inicializa todos los datos requeridos
        public Monitor(Semaphore sillaDisponible) {
            super();
            this.sillaDisponible = sillaDisponible;
            this.ocupado = false;
            this.random = new Random();
        }

        public void run() {
            // Correr continuamente
            while (true) {
                try {
                    sillaDisponible.acquire(); // Adquiere el semáforo antes de modificar sillasLibres
                    setOcupado(true);
                    // Simular atendiendo dudas del estudiante con un tiempo aleatorio
                    sleep(random.nextInt(3000) + 1000);
                    setOcupado(false);
                    sillasLibres += 1; // Aumenta en uno el número de sillas libres.
                    sillaDisponible.release(); // Libera el semáforo después de modificar sillasLibres
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public boolean isOcupado() {
            return ocupado;
        }

        public void setOcupado(boolean ocupado) {
            this.ocupado = ocupado;
        }
    }

    public static class Estudiante extends Thread {
        private int numCliente; // Número de estudiante
        private Monitor monitor; // Referencia al monitor
        private Semaphore sillasAccesibles; // Semáforo para controlar el acceso a las sillas disponibles
        private Semaphore monitorDisponible; // Semáforo para indicar si el monitor está disponible
        private Semaphore servicioTerminado; // Semáforo para indicar que el servicio ha terminado

        public Estudiante(int nCli, Monitor monitor, Semaphore sillasAccesibles, Semaphore monitorDisponible, Semaphore servicioTerminado) {
            this.numCliente = nCli;
            this.monitor = monitor;
            this.sillasAccesibles = sillasAccesibles;
            this.monitorDisponible = monitorDisponible;
            this.servicioTerminado = servicioTerminado; // Inicializar el nuevo semáforo
        }

        public void run() {
            while (true) {
                try {
                    sillasAccesibles.acquire(); // Adquiere el semáforo para controlar el acceso a las sillas disponibles
                    if (sillasLibres > 0) { // Verifica si hay sillas libres
                        sillasLibres--; // Reduce el número de sillas libres
                        sillasAccesibles.release(); // Libera el semáforo para controlar el acceso a las sillas disponibles
                        monitorDisponible.acquire(); // Adquiere el semáforo para indicar si el monitor está disponible

                        System.out.println("Estudiante " + numCliente + " esta siendo atendido por el monitor.");

                        // Simula el tiempo que lleva la atención del monitor (entre 1 y 2.5 segundos)
                        Random rand = new Random();
                        Thread.sleep(rand.nextInt(1500) + 1000);

                        System.out.println("Estudiante " + numCliente + " ha terminado y se va pa su casitaaaaaa.");
                        monitorDisponible.release(); // Libera el semáforo para indicar si el monitor está disponible
                        break;
                    } else {
                        sillasAccesibles.release();
                        System.out.println("No hay sillas libres. Estudiante " + numCliente + " se va a progamar a la sala de computo");

                        Random rand = new Random();
                        Thread.sleep(rand.nextInt(2001) + 2000);

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        // Semáforo para controlar el acceso a la silla del monitor
        Semaphore sillaDisponible = new Semaphore(1);

        // Semáforo para controlar el acceso a la sección crítica (sillas libres)
        Semaphore sillasAccesibles = new Semaphore(1);

        // Semáforo para indicar si el monitor está disponible
        Semaphore monitorDisponible = new Semaphore(1);

        // Semáforo para indicar que el servicio ha terminado
        Semaphore servicioTerminado = new Semaphore(0);

        // Crear una instancia del Monitor
        Monitor monitor = new Monitor(sillaDisponible);
        monitor.start(); // Iniciar el hilo del monitor

        // Crear instancias de estudiantes
        for (int i = 0; i <= 7; i++) { 
            Estudiante estudiante = new Estudiante(i + 1, monitor, sillasAccesibles, monitorDisponible, servicioTerminado);
            estudiante.start(); // Iniciar el hilo de cada estudiante
            try {
                Thread.sleep(1000); // Retraso de 1 segundo en la creacion de cada Hilo Estudiante (1000 milisegundos)
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
