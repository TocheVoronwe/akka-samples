package sample.cluster.transformation;

public class App {

  public static final int NB_MAPPERS = 3;
  public static final int NB_REDUCERS = 4;
  public static final String MASTER_SYSTEM = "Master_System";
  public static final String CLUSTER_SYSTEM = "ClusterSystem";
  public static final String CLUSTER_PORT = "ClusterSystem";
  public static final String REDUCERS_PATH = "akka.tcp://" + CLUSTER_SYSTEM + "@127.0.0.1:" + CLUSTER_PORT;

  public static void main(String[] args) {
    // starting 2 frontend nodes and 3 backend nodes
    StartReducers.startMapper("2550");
    StartMappers.startMapper("2551" );
/*    StartMappers.startMapper("2552" );
    StartMappers.startMapper("2553");*/
    StartMasters.startMasters("2554");
  }
}
