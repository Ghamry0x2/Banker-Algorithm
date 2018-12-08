package banker.algorithm;

import java.util.*;

public class bankerAlg {
    
    //Number of processes
    private int n;
    
    //Number of resources types
    private int m; 
    
    //Sysytem State
    private boolean safe;
    
    //Vector of length m. If available [j] = k, there are k instances of resource type Rj available
    private int available [] ; 
    
    //n x m matrix.  If Max [i,j] = k, then process Pi may request at most k instances of resource type Rj
    private int max[][]; 
    
    //n x m matrix.  If Allocation[i,j] = k then Pi is currently allocated k instances of Rj
    private int allocation[][];
    
    //n x m matrix. If Need[i,j] = k, then Pi may need k more instances of Rj to complete its task ,
    //Need [i,j] = Max[i,j] – Allocation [i,j]
    private int need[][]; 
    
    //holds all resources released by processes
    private int released[][];
    
    //for number of processes n, finish[i] indicates whether the process i finished its processing or not 
    private boolean[] finish; 
    
    private int[] reqArr;

    private Scanner ss = new Scanner (System.in);
    private Random rand = new Random();

    public bankerAlg(int n, int m) {
        
        this.n = n;
        this.m = m;
        this.safe = true;
        
        max = new int[n][m];
        allocation = new int[n][m];
        released = new int[n][m];
        need = new int[n][m];
       
        available = new int[m];
        reqArr = new int[m];
        
        finish = new boolean[n];
        
        init();
        
    }
    
    private void init(){
       
        // Intialize Available Array
        System.out.println("Fill Available Matrix (1x" + m + "):");
        for(int i = 0; i < m; i++){
            available[i] = ss.nextInt(); 
        }  
        
        // Intialize Max Array
        //System.out.println("Fill Max Matrix: " + "("+n+"x"+m+")");
        for(int i = 0; i < n; i++){
            for(int j = 0; j < m; j++){
                need[i][j] = max[i][j] = rand.nextInt(available[j]+1);
            }
        }
        
        // Intialize Allocation Array
        //System.out.println("Fill Allocation Matrix: " + "("+n+"x"+m+")");
        for(int i = 0; i < n; i++){
            for(int j = 0; j < m; j++){
                allocation[i][j] = 0;
                released[i][j] = 0;
            }
        }  
         
        // Finish array elements are already intiallized by "false", but,
        // If the randomly generated needs of a process is 0 0 0, Then we need to update its finish array by true
        for(int i=0; i<n; i++)
            updateFinish(i);
        
        
        printMaxArray();
        printAllocationArray();
        
        start();
    }
    
    private void start(){
        do{
            int p = rand.nextInt(n);
            // If the selected process already finished its working, select another process
            if( finish[p] ){
                continue;
            }
            
            display(p);
            request(p);
                
            boolean canBeSatisfied = canBeSatisfied();
            if( canBeSatisfied ){
                allocate(p);
                safe = isSafe();                
            }
            System.out.println("Valid Request: " + (canBeSatisfied && safe ));

            if( canBeSatisfied ){
                if( safe ){
                    System.out.println("Request Accepted...");
                    updateFinish(p);
                }
                else{
                    // Observation
                    System.out.println("This Request Will Lead The System To Unsafe State");

                    // Printing the needTemp array
                    System.out.println("The Need Array After Accepting the Request: ");
                    printNeedArray();
                    
                    // Printing the availableTemp array
                    System.out.println("\nThe Available Array After Accepting The Request:");
                    printAvailableArray();
                    
                    System.out.println("\nThe Allocation Array After Accepting The Request:");
                    printAllocationArray();
                    
                    // Conclusion
                    System.out.println("\nCan't Fullfill The Needs of Any Process ");
                    System.out.println("Unsafe State, Request Rejected...");
                    
                    revAllocation(p);
                }
            }
            else {
                System.out.println("No Enough Resources, Request Rejected...");
            }

            
            
            if(!isAllocationEmpty())
                randomRelease();
            
        }
        while(!isNeedEmpty());
        
        System.out.println("");
        for(int i=0; i<25; i++)
            System.out.print("--");
        System.out.println("\nAll Processes Finished Working Successfully");
        System.out.println("");
    }
    
    private void allocate(int p){
        for(int i = 0; i < m; i++){
            allocation[p][i] += reqArr[i];
            available[i] -= reqArr[i];
            need[p][i] = max[p][i] - allocation[p][i] - released[p][i];
        }
    }
    
    private void revAllocation(int p){
        for(int i = 0; i < m; i++){
            allocation[p][i] -= reqArr[i];
            available[i] += reqArr[i];
            need[p][i] = max[p][i] - allocation[p][i] - released[p][i];
        }
    }
    
    private void request(int p){
        
        int count = 0;
        
        do{
            for(int i=0; i<m; i++){
                reqArr[i] = rand.nextInt(need[p][i]+1);
            }


            for (int i = 0; i < reqArr.length; i++) {
                count += reqArr[i];
            }
        }
        while(count == 0);

        System.out.println("P" + p + " requested:");
        for(int h: reqArr){
            System.out.print(h + " ");
        }
        System.out.println("");
        
    }
    
    private boolean canBeSatisfied(){
        for(int j = 0; j < m; j++) {
            //if(available[j] < reqArr[j] + allocation[p][j])
            if(available[j] < reqArr[j])
            return false;
            //System.out.println("allocated: " + allocation[p][j]);
        }
        return true;
    }
    
    private boolean check(int i, int[] availableTemp){
        //checking if all resources for it^(h) process can be allocated
        for(int j=0;j<m;j++) {
            if(availableTemp[j]<need[i][j]) {
                return false;
            }
        }
        return true;
    }
    
    private boolean isSafe(){
        boolean done[] = new boolean[n];
        int j = 0;
        
        int[] availableTemp = Arrays.copyOf(available, m);

        while(j<n){  //loop until all process allocated
            boolean allocated=false;
            for(int i=0;i<n;i++)
                if(!done[i] && check(i, availableTemp)){  //trying to allocate
                    for(int k=0;k<m;k++)
                        availableTemp[k] += allocation[i][k];
                    allocated = done[i] = true;
                    j++;
                }
               if(!allocated) break;  //if no allocation
        }
        
        return j==n;
    }
    
    private void updateFinish(int p){
        int count;
        count = 0;
        for(int j=0; j<m; j++){
            if((need[p][j] == 0)) {
                count++;
            }
        }
        if(count == m) {
            finish[p] = true;
            release(p);
        }
    }
    
    private void release(int p){
        System.out.println("Process P" +p+ " Finished Working Successfully...");
        for(int i=0; i<m; i++){
            releaseResource(p, i);
        }
    }
    
    private void releaseResource(int p, int m){
        //Update Available Array
        available[m] += allocation[p][m];
        released[p][m] += allocation[p][m];
        //Update Allocation Array
        allocation[p][m] = 0;
    } 

    private void randomRelease(){
        int i = rand.nextInt(n),j = rand.nextInt(m);
        while(allocation[i][j] == 0){
            i = rand.nextInt(n);
            j = rand.nextInt(m);
        }
        System.out.println("P" + i + " released: " + allocation[i][j] + " of R" + j);
        releaseResource(i, j);
        
        
    }
    
    private void display(int p){
        System.out.println("");
        
        System.out.println("Finished Processes:");
        for(int i=0; i<n; i++){
            if(finish[i])
                System.out.print("P" + i + " ");
        }
        System.out.println("");
        
        for(int i=0; i<50; i++)
            System.out.print("-");
        
        System.out.println("");
        
        System.out.println("P" + p + " max:");
        for(int s=0; s<m; s++){
            System.out.print(max[p][s] + " ");
        }
        System.out.println("");

        System.out.println("P" + p + " need:");
        for(int s=0; s<m; s++){
            System.out.print(need[p][s] + " ");
        }
        System.out.println("");                

        System.out.println("Available Resources:");
        for(int s=0; s<m; s++){
            System.out.print(available[s] + " ");
        }
        System.out.println("");
        
    }
    
    private void printMaxArray(){
        
        System.out.println("\nThe Max Array: ");
        
        for (int i = 0; i < n; i++) {
            
            System.out.print("P" + i + " : ");
            for (int j = 0; j < m; j++) {
                System.out.print(max[i][j] + " ");
            }
            
            System.out.println("");
        }

    }
    
    private void printAllocationArray(){
        
        System.out.println("Allocation Array: ");
        
        for (int i = 0; i < n; i++) {
            
            System.out.print("P" + i + " : ");
            for (int j = 0; j < m; j++) {
                System.out.print(allocation[i][j] + " ");
            }
            
            System.out.println("");
        }
        
    }
    
    private boolean isNeedEmpty(){
        for(int i=0; i<n; i++){
            for(int j=0; j<m; j++){
                if(need[i][j] != 0)
                    return false;
            }
        }
        return true;
    }
    
    private boolean isAllocationEmpty(){
        for(int i=0; i<n; i++){
            for(int j=0; j<m; j++){
                if(allocation[i][j] != 0)
                    return false;
            }
        }
        return true;
    }
    
    private void printNeedArray(){
        for (int i = 0; i < n; i++) {
            System.out.print("P" + i + " : ");
            for (int j = 0; j < m; j++) {
                System.out.print(need[i][j] + " ");
            }
            if( i != n-1 )
                System.out.println("");
        }
    }
    
    private void printAvailableArray(){
        System.out.println("Available Resources: ");
        for (int i = 0; i < m; i++) {
            System.out.print(available[i] + " ");
        }
    }
}