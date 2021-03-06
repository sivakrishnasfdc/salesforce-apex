public class MapUtil {

    public static Map<ID, Integer> mapUpdate(Map<ID, Integer> m, ID id, Integer i, String opt) {
        if (opt == '$inc'){
            if (m.containsKey(id)){
                m.put(id, m.get(id) + i);
            }
        else{
            m.put(id, i);
        }
        }
        return m;
    }
    
    public static Map<Id, Set<Lead>> mapUpdate(Map<ID, Set<Lead>> m, ID id, Set<Lead> lead, String opt) {
       if (opt == 'add'){
            if (m.containsKey(id)){
                for (Lead aLead : lead) {
                    (m.get(id)).add(aLead);
                }
            }
        else{
            m.put(id, lead);
        }
        }
        return m;
    }
    
    public static Map<ID, Set<Lead>> mapUpdate(Map<ID, Set<Lead>> m, ID id, Lead lead, String opt) {
        Map<Id, Set<Lead>> newMap = mapUpdate(m, id, new Set<Lead>{lead}, opt);
        return newMap;
    }
    
    
    
    
 }