import java.io.*;


public class Naloga1 {

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.exit(1);
        }

        BufferedReader vhod = new BufferedReader(new FileReader(args[0]));
        PrintWriter izhod = new PrintWriter(new FileWriter(args[1]));

        String prva_vrstica = vhod.readLine();

        int N = Integer.parseInt(prva_vrstica.split(",")[0]);

        int[][] plantaze = new int[N][N];

        for (int i = 0; i < N; i++) {
            String vrstica = vhod.readLine();
            
            String[] vrstice = vrstica.split(",");

            int[] vrstice_int = new int[vrstice.length];
            for(int j = 0; j < vrstice.length; j++) {
                vrstice_int[j] = Integer.parseInt(vrstice[j]);       
            }
            plantaze[i] = vrstice_int;
        }

        boolean[][] p = new boolean[N][N];

        izhod.printf("%d\n", pot(plantaze, 0, 0, p));

        vhod.close();
        izhod.close();
    }

    static boolean reseno = false;

    public static int pot(int[][] plantaze, int i, int j, boolean[][] p) {
        int N = plantaze.length - 1;
        int vzponi = 0;
        
        
        if (i == N && j == N) {
            reseno = true;
            p[i][j] = true;
            return 0;
        }
        
        if (p[i][j]) {
            return 0;
        }
        
        p[i][j] = true;

        if (!reseno) {
            
            if (j < N) {
                int razlika_desno = plantaze[i][j+1] - plantaze[i][j];
                if (razlika_desno <= 20 && razlika_desno >= -30) {
                    vzponi = pot(plantaze, i, j+1, p) + (razlika_desno > 0 ? razlika_desno : 0);
                    if (reseno)
                        return vzponi;
                }
            }
            
            if (i < N) {
                int razlika_dol = plantaze[i+1][j] - plantaze[i][j];
                if (razlika_dol <= 20 && razlika_dol >= -30) {
                    vzponi = pot(plantaze, i+1, j, p) + (razlika_dol > 0 ? razlika_dol : 0);
                    if (reseno)
                        return vzponi;
                }
            } 
            
            if (i > 0) {
                int razlika_gor = plantaze[i-1][j] - plantaze[i][j];
                if (razlika_gor <= 20 && razlika_gor >= -30) {
                    vzponi = pot(plantaze, i-1, j, p) + (razlika_gor > 0 ? razlika_gor : 0);
                    if (reseno)
                        return vzponi;
                } 
            }
            if (j > 0) {
                int razlika_levo = plantaze[i][j-1] - plantaze[i][j];
                if (razlika_levo <= 20 && razlika_levo >= -30) {
                    vzponi = pot(plantaze, i, j-1, p) + (razlika_levo > 0 ? razlika_levo : 0);
                    if (reseno)
                        return vzponi;
                }
            }
        }
        
        return vzponi;
    }
    
    public static void izpisiPolje(boolean[][] p, int N) {
        for (int i = 0; i <= N; i++) {
            for (int j = 0; j <= N; j++) {
                if (p[i][j])
                    System.out.printf("[O]");
                else
                    System.out.printf("[-]");        
            }
            System.out.println();
        }
    }
}