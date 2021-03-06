package com.ajaya.cashloan.core.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2015/4/22.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ListUtil {
	
	private final static Logger logger = LoggerFactory.getLogger(ListUtil.class);
	public static List<Map<String,Object>> treeForExt(List<Map<String,Object>> list,Boolean leafChecked,Boolean rootChecked,Boolean expanded){
        for(Map rec:list){
        	List<Map<String,Object>> children= (List<Map<String,Object>>) rec.get("children");
            if(children==null || children.size()==0){
                rec.put("leaf",true);
                if(leafChecked!=null){
                    rec.put("checked",leafChecked);
                }
            }else{
                if(rootChecked!=null){
                    rec.put("checked",rootChecked);
                }
                if(expanded!=null){
                    rec.put("expanded",expanded);
                }
                treeForExt(children,leafChecked,rootChecked,expanded);
            }
        }
        return list;
    }
	public static List<Map<String,Object>> list2Tree(List<Map<String,Object>> list,String idField,String parentField){
    	List<Map<String,Object>> roots=new ArrayList<Map<String,Object>>();
        Map<String,Map> indexor=new LinkedHashMap<String, Map>();
        for(Map rec:list){
            indexor.put(rec.get(idField).toString(),rec);
        }
        for(Map rec:list){
        	rec.put(idField, rec.get(idField).toString());
            Map father=indexor.get(String.valueOf(rec.get(parentField)));
            if(father!=null){
                List<Map> children= (List<Map>) father.get("children");
                if(children==null){
                    children=new ArrayList<Map>();
                    father.put("children",children);
                }
                children.add(rec);
            }else{
                roots.add(rec);
            }
        }
        return roots;
    }
    
    public static List<Map> listCovertToTree(List<Map> list,String idField,String parentField){
        List<Map> roots=new ArrayList<Map>();
        Map<String,Map<String, Object>> indexor=new LinkedHashMap<String, Map<String, Object>>();
        for(Map<String, Object> rec:list){
            indexor.put(rec.get(idField).toString(),rec);
        }
        for(Map<String, Object> rec:list){
            Map<String, Object> father=indexor.get(String.valueOf(rec.get(parentField)));
            if(father!=null){
				List<Map<String, Object>> children= (List<Map<String, Object>>) father.get("children");
                if(children==null){
                    children=new ArrayList<Map<String, Object>>();
                    father.put("children",children);
                }
                children.add(rec);
            }else{
                roots.add(rec);
            }
        }
        return roots;
    }
    
	public static List<Map<String,Object>> treeCovertToExt(List<Map<String,Object>> list,Boolean leafChecked,Boolean rootChecked,Boolean expanded){
        for(Map<String,Object> rec:list){
            List<Map<String,Object>> children= (List<Map<String,Object>>) rec.get("children");
            if(children==null || children.size()==0){
                rec.put("leaf",true);
                if(leafChecked!=null){
                    rec.put("checked",leafChecked);
                }
            }else{
                if(rootChecked!=null){
                    rec.put("checked",rootChecked);
                }
                if(expanded!=null){
                    rec.put("expanded",expanded);
                }
                treeCovertToExt(children,leafChecked,rootChecked,expanded);
            }
        }
        return list;
    }

    public static String join(List list){
        return join(list,",");
    }
    public static String join(List list,String str){
        StringBuilder sb=new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
             Object o=list.get(i);
            if(i>0)sb.append(str);
            sb.append(o.toString());
        }
        return sb.toString();
    }

	public static long attachCnt(List<Map<String, Object>> list,String textField,String cntField){
        long cnt=0;
        for(Map<String, Object> rec:list){
            List<Map<String, Object>> children=  (List<Map<String, Object>>) rec.get("children");
            if(children==null || children.size()==0){
                rec.put("leaf",true);
                rec.put(textField+"Cnt",rec.get(textField)+"("+rec.get(cntField)+")");
                cnt+=Long.valueOf(rec.get(cntField).toString());
            }else{
                long c=attachCnt(children,textField,cntField);
                rec.put("expanded",true);
                rec.put(textField+"Cnt",rec.get(textField)+"("+c+")");
                cnt+=c;
            }
        }
        return cnt;
    }


    public static Set<Object> collectProperty(List<Map> list,String property){
        Set<Object> sets=new LinkedHashSet<Object>();
        for (Map map : list) {
            sets.add(map.get(property));
        }
        return sets;
    }
    
    public static List<Object> quchong(List list){
		Set set = new  HashSet(); 
        List newList = new  ArrayList(); 
        set.addAll(list);
        newList.addAll(set);
        return newList;
	}

    
    public static List<String> quKongGe(List<String> list){
    	List<String> phoneList = new ArrayList<>();
    	if(list != null && list.size() > 0){
    		for (String phone : list) {
    			phoneList.add(phone.replace(" ", ""));
			}
    	}
    	return phoneList;
    }
    
    //????????????????????????List
    public static List<String>  parseConfigFileToList(String configFileName){
    	List<String> list = null;
    	try {
			list = new ArrayList<String>();
			String pathname = ListUtil.class.getResource("/config/file/" + configFileName + ".txt").getFile();
			File filename = new File(pathname);
			InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
			BufferedReader br = new BufferedReader(reader);
			String line = "";
			while ((line = br.readLine()) != null) {
				list.add(line);
			} 
		} catch (Exception e) {
			logger.info("????????????:" + configFileName + ",??????List??????:", e);
		}
		return list;
    }
}
