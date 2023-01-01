package com.coder.community;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.*;


class ans{
    String answer;
    double proper;

    public ans(String answer, double proper){
        this.answer = answer;
        this.proper = proper;
    }

    @Override
    public String toString() {
        return "ans{" +
                "answer='" + answer + '\'' +
                ", proper=" + proper +
                '}';
    }
}
public class AlphaTest {
        @Test
        public void test1() throws IOException {
//        System.out.println(getAnswer("D:/Desktop/思政.txt","马克思主义中国化具体是什么"));
        //System.out.println(getAnswer("D:/Desktop/思政.txt","你在干嘛"));
            int [] pushed = new int[]{1,2,3,4,5};
            int [] poped = new int[]{4,5,3,2,1};
            validateStackSequences(pushed,poped);
        }
    public PriorityQueue<ans> getAnswer(String fileName, String question) throws IOException {
        File file = new File(fileName);
        FileReader reader = null;
        StringBuilder all = new StringBuilder();
//        Set<Character>set = new HashSet<>();
//        for(int i = 0; i < question.length();i++){
//            set.add(question.charAt(i));
//        }
        PriorityQueue<ans> answer = new PriorityQueue<>(new Comparator<ans>() {
            @Override
            public int compare(ans o1, ans o2) {
                return o2.proper - o1.proper > 0 ? 1 : -1;
            }
        });
//        List<String> answer = new ArrayList<>();
        if (file.exists()) {
            try {
                reader = new FileReader(file);
                char[] get = new char[1024];
                int len;
                while ((len = reader.read(get)) != -1) {
                    all.append(get, 0, len);
                }
                String[] split = all.toString().split("[*][*][*]");
                for (String pro : split) {
                    String title = pro.substring(0, pro.indexOf('\n'));
                    Set<Character> set = new HashSet<>();
                    int n = 0;
                    for (int i = 0; i < title.length(); i++) {
                        set.add(title.charAt(i));
                    }
                    for (int i = 0; i < question.length(); i++) {
                        char c = question.charAt(i);
                        if (set.contains(c)) {
                            n++;
                        }
                    }
                    double proper = (double) n / question.length();
                    if (proper > 0.6) {
                        answer.add(new ans(pro.substring(pro.indexOf('\n')), proper));
                    }
//            System.out.println(title);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                reader.close();
            }
        }
//        ArrayList<String> a = new ArrayList<>(List.of(split));
//        System.out.println(a);
        return answer;
    }

    public boolean validateStackSequences(int[] pushed, int[] popped) {
        Deque<Integer>stack = new LinkedList<>();
        int pushI = 0;
        int popI = 0;
        while(popI < popped.length){
            if(stack.size()!=0 && stack.peek() == popped[popI]){
                stack.pop();
                popI++;
            }
            else if(pushI < pushed.length){
                stack.push(pushed[pushI++]);
            }
            else{
                return false;
            }
        }
        return true;
    }
    @Test
    public  void test2(){
            largestRectangleArea(new int[]{2,0,2});
            HashMap<Integer,Integer> a = new HashMap<>();
    }
    public int largestRectangleArea(int[] heights) {
        int n = heights.length;
        int[] left = new int[n];
        int[] right = new int[n];
        Arrays.fill(right, n);

        Deque<Integer> mono_stack = new ArrayDeque<Integer>();
        for (int i = 0; i < n; ++i) {
            while (!mono_stack.isEmpty() && heights[mono_stack.peek()] >= heights[i]) {
                right[mono_stack.peek()] = i;
                mono_stack.pop();
            }
            left[i] = (mono_stack.isEmpty() ? -1 : mono_stack.peek());
            mono_stack.push(i);
        }
        StringBuilder a = new StringBuilder();
        String s = new String();
        int ans = 0;
        for (int i = 0; i < n; ++i) {
            ans = Math.max(ans, (right[i] - left[i] - 1) * heights[i]);
        }
        return ans;

    }
}