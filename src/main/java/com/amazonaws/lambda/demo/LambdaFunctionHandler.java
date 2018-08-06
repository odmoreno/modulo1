package com.amazonaws.lambda.demo;

import java.util.ArrayList;
import java.util.Map;

import org.json.simple.JSONArray;

import umontreal.ssj.rng.RandomStream;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class LambdaFunctionHandler implements RequestHandler<Map<String, Object>, String> {

    @Override
    public String handleRequest(Map<String, Object> input, Context context) {
        context.getLogger().log("Input: " + input);
        String msg= "";
        
        ArrayList<UtilityFunction> functions = new ArrayList<UtilityFunction>();
        ArrayList mrc, point, alto;
        double x1, x2;
        
        App al;
        
        try {
        	
        	int mem = (int)input.get("totalMemory");
        	double M = (double) mem;
            //double M = (double) Mem;
            ArrayList weights = (ArrayList) input.get("weights");
            ArrayList memory = (ArrayList) input.get("minimumMemory");
            ArrayList kj = (ArrayList) input.get("kj");
            int k = (int) kj.get(0);
            int j = (int) kj.get(1);
            
            ArrayList bdi = (ArrayList) input.get("cdi");
            ArrayList cdi = (ArrayList) input.get("cdi");

            
            double[] pesos = new double[weights.size()];
            double[] memoria = new double[memory.size()];
            
            double[] _bdi = new double[bdi.size()];
            double[] _cdi = new double[cdi.size()];
            double[] sizeMemory = new double[memory.size()];

            for (int i = 0; i < weights.size(); ++i) {
            	int x = (int)weights.get(i);
                double d = (double)x;
                pesos [i] =  d;
            }
            for (int i = 0; i < memory.size(); ++i) {
            	int x = (int)memory.get(i);
                double d = (double)x;
                memoria [i] =  d;
            }
            
            for (int i = 0; i < bdi.size(); ++i) {
            	if (bdi.get(i) instanceof Integer) {
            		int x = (int)bdi.get(i);
                    double d = (double)x;
                    _bdi [i] =  d;
            	}
            	else {
            		double d = (double)bdi.get(i);
                    _bdi [i] =  d;
            	}
            }
            
            for (int i = 0; i < cdi.size(); ++i) {
            	if (cdi.get(i) instanceof Integer) {
            		int x = (int)cdi.get(i);
                    double d = (double)x;
                    _cdi [i] =  d;
            	}
            	else {
            		double d = (double)cdi.get(i);
                    _cdi [i] =  d;
            	}
            }
           
            ArrayList list = (ArrayList)  input.get("mrc");
            
            if (list != null) {
                int len = list.size();
                for (int i=0; i<len; i++) {
                    //System.out.println(" " + list.get(i));
                    mrc = (ArrayList) list.get(i);
                    UtilityFunction f = new UtilityFunction();
                    
                    alto = (ArrayList) mrc.get(mrc.size()-1);
                    double d1 = (double) (alto.get(0));
                    sizeMemory[i] = d1;
                    
                    for (int z=0; z < mrc.size(); z++){
                        //System.out.println(" " + mrc.get(z));
                        point =  (ArrayList) mrc.get(z);
                        if (point.get(1) instanceof Integer ) {
                        	int x = (int)point.get(1);
                        	double d = (double)x;
                        	x1 = (double) (point.get(0));
                        	//x2 = d;
                        	x2 = 1 - d;

                        }
                        else {
                        	x1 = (double) (point.get(0));
                            //x2 = (double) point.get(1);
                            x2 = 1 -(double) point.get(1);
                        }
                        f.addPoint((double)x1, x2);
                    }
                    functions.add(f);
                }
            }
            
            al = new App(4);
            long startTime = System.nanoTime();
            double totalRepartido = 0;
            String resultado = "", datos = "";
            double[] result = al.probabilisticAdactiveSearch(k ,j ,functions, pesos,500, M, memoria, _bdi, _cdi, sizeMemory);
            for (int i=0;i< result.length; i++ ) {
                //System.out.println(result[i]);
                resultado += result[i] + " ,";
                totalRepartido += result[i];
            }
            long endTime = System.nanoTime();
            double totalTime = endTime - startTime;
            double time = totalTime/ 1000000000.0;
            totalRepartido = totalRepartido / M;

            datos += time + "," + totalRepartido + "," ;
            msg = datos + resultado;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
          
        
        // TODO: implement your handler
        return msg;
    }

}
