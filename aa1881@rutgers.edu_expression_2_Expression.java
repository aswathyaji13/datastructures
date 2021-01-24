package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	/** DO NOT create new vars and arrays - they are already created before being sent in
    	 ** to this method - you just need to fill them in.
    	 **/
    	String temp1 = new String();
    	
    	if(temp1.isEmpty() == false) {
    		if(Character.isDigit(temp1.charAt(0))==false) {
    			
    			Variable v = new Variable(temp1);
    			
    			if(vars.contains(v)==false) {
    				vars.add(v);
    			}
    		}
    	}
    	for(int i = 0; i <= expr.length()-1;i++) {
    		
    		String temp2 = String.valueOf(expr.charAt(i));
    		
    		if(delims.contains(temp2)) {
    			
    			if(temp1.isEmpty() == true) {
    				continue;
    			}
    			
    			if(temp2.equals("[")==true) {
    				Array a = new Array(temp1);
    				if(arrays.contains(a)==false) {
    					arrays.add(a);
    				}
    			}
    			
    			else {
    				
    				if(Character.isDigit(temp1.charAt(0))==true) {
    					temp1 = new String();
    					continue;
    				}
    				
    				Variable v = new Variable(temp1);
    				if(vars.contains(v)==false) {
    					vars.add(v);
    				}
    			}
    			temp1 = new String();
    		}
    		
    		else {
    			temp1 += temp2;	
    		}
    	}
    	
    }
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	Stack<String> comp = new Stack<String>();
    	Stack<Float> digits = new Stack<Float>();
    	//pointer
    	StringTokenizer arrow = new StringTokenizer(takeOut(expr),delims,true);
    	while(arrow.hasMoreTokens()) { 	
    		
    		String now = arrow.nextToken();
    		int i = now.charAt(0);
    		
    		if( i ==  '(' || delims.indexOf(i) < 0) {
    			if(delims.indexOf(i) < 0) {
	    			if((i <= 'Z' &&i >= 'A' ) || (i <= 'z'&& i >= 'a' )) {
	    				Variable tempV = new Variable(now);
	    				Array tempA = new Array(now);
	    				if(arrays.contains(tempA)) {
	    					int pos = (int) Math.floor(evaluate(expected(arrow, arrow.nextToken()),vars,arrays));
	    					digits.push((float) arrays.get(arrays.indexOf(tempA)).values[pos]);	
	    					
	    				}
	    				else if(vars.contains(tempV)) {
	    					digits.push((float) (vars.get(vars.indexOf(tempV)).value));
	    				}
	    			}
	    			else {
	    				digits.push(Float.parseFloat(now));
	    			}
    			}
    			else {
    				//uses recursion
    				float count = evaluate(expected(arrow, "("),vars,arrays);
    				digits.push(count);
    			}
    			
    			if(comp.isEmpty()== false) {
    				String pos1 = comp.pop();
    				switch (pos1) {
    					case"-": digits.push(-1*digits.pop()); comp.push("+"); 
    						break;
    					case  "*": digits.push(digits.pop()*digits.pop()); 
    						break;
    					case "/": digits.push(1/(digits.pop()/digits.pop()));
    						break;
    					default: comp.push(pos1); 
    				}
    			}
    		}

    		else if(delims.indexOf(i) < 6) {
    			comp.push(now);
    		}

    	}
    	
    	//while loop running through the stack
    	while(digits.size() > 1) {
    		digits.push(digits.pop()+digits.pop());
    	}
    	//solution cant display if stack is empty
    	return digits.pop();  
    }

    // checks where there are spaces and takes them out
    private static String takeOut(String space) {
    	if(space.length() == 0) {
    		return "";
    	}
    	else if(space.substring(0,1).equals(" ")) {
    		return takeOut(space.substring(1));
    	}
    	
    	else {
    		return space.substring(0,1) + takeOut(space.substring(1));
    	}
    }
    
    //returns expected
    private static String expected(StringTokenizer tok, String s) { 	
    	String sol = "";
    	Stack<String> check = new Stack<String>();
    	check.push(s);
    	
		while((tok.hasMoreTokens() && check.isEmpty()) == false) {
			
			String nextTok = tok.nextToken();
			
	    	if(nextTok.equals("]") || nextTok.equals(")") ) {
	    		check.pop();
	    	}
	    	else if(nextTok.equals("[") || nextTok.equals("(") ) {
	    		check.push(nextTok);
	    	}

			if(check.isEmpty()) {
				break;
			}
			sol += nextTok;
		}
		return sol;
	} 	
    
}
