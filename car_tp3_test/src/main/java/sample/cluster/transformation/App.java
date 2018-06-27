package sample.cluster.transformation;

public class App {

  public static final int NB_MAPPERS = 3;
  public static final int NB_REDUCERS = 4;
  public static final String MASTER_SYSTEM = "Master_System";
  public static final String REDUCERS_PATH = "akka.tcp://" + MASTER_SYSTEM + "@127.0.0.1:3001";

  public static void main(String[] args) {
    // starting 2 frontend nodes and 3 backend nodes
    StartMappers.startMapper("2551" );
    StartMappers.startMapper("2552" );
    StartMappers.startMapper("0");
    StartMasters.startMasters("0");
  }
}
