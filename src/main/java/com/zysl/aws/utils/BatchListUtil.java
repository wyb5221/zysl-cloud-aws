package com.zysl.aws.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatchListUtil<E> {
    /**
     * 把list分成多个批次
     * @param list 集合
     * @param batchSize 拆分个数
     * @return Map<Integer,List<E>>
     */
    public Map<Integer,List<E>> batchList(List<E> list, int batchSize){
        Map<Integer,List<E>> itemMap = new HashMap<>();
        List<E> listnew=new ArrayList<E>();
        int j = 1;
        int size = list.size() / batchSize;
        for(int i = 0;i<  list.size();i++){
            if(i == 0 ||i%size == 0) {
                listnew = new ArrayList<E>();
                listnew.add(list.get(i));
                itemMap.put(j, listnew);
                j++;
            }else {
                listnew.add(list.get(i));
            }
        }
        return itemMap;
    }
}
