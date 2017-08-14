package segment;

import java.util.Scanner;

public class CalMetrix {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        int i = sc.nextInt();
        int j = sc.nextInt();
        int k = sc.nextInt();

        String[] matrix = new String[k];
        for(int a = 0;a < k;a++){
            if(sc.nextLine() != ""){
                int count = 0;
                if(a == 0){
                    matrix[a] = sc.nextLine();
                }
                else{
                    matrix[a] = "";
                }

                while(count < i){

                    matrix[a] = matrix[a] + sc.nextLine();
                    count++;
                }
            }
        }

        int judge = 0; //judge the first different char,1 or 2 is same,3 is different
        int cou = 0;  //memory the position of the first different char
        String[] judgeMatrix = new  String[k];
        for(int c = 0;c < i*j;c++){
            if(judge != 2){
                for(int b = 0;b < k;b++){
                    if(matrix[b].charAt(c) == '1'){
                        if(b == k - 1){
                            judge = 1;
                        }
                        continue;
                    }
                    else{
                        break;
                    }
                }
            }

            if(judge != 1){
                for(int b = 0;b < k;b++){
                    if(matrix[b].charAt(c) == '0'){
                        if(b == k - 1){
                            judge = 2;
                        }
                        continue;
                    }
                    else{
                        break;
                    }
                }

            }

            if(judge != 1 && judge != 2){
                cou = c;
                break;
            }
            else{
                judge = 0;
            }

        }

        //start to record the different string
        for(int a = 0;a < k;a++){
            judgeMatrix[a] =  "";

        }
        while(cou < i*j){
            for(int b = 0;b < k;b++){
                judgeMatrix[b] = judgeMatrix[b] + matrix[b].charAt(cou);
            }
            cou++;
            for(int b = 0;b < k-1;b++){
                for(int c = b + 1;c < k;c++){
                    if(judgeMatrix[b].equals(judgeMatrix[c])){
                        judge = 4;
                        break;
                    }
                }
                if(judge == 4){
                    judge = 0;
                    break;
                }
                else if(judge != 4 && b == k-2){
                    judge = 5;
                }
            }
            if(judge == 5){
                break;
            }
        }
        System.out.println(judgeMatrix[0].length());
    }
}


