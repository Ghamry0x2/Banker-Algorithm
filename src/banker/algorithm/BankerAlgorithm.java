package banker.algorithm;

import java.util.*;


public class BankerAlgorithm {

    
    public static void main(String[] args) {
       int n ,m;
       System.out.println("How Many Processes and Resources?");
       Scanner s = new Scanner(System.in);
       n = s.nextInt();
       m = s.nextInt();
       bankerAlg b = new bankerAlg(n,m);
    }
    
}
