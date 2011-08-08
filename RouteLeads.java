trigger RouteLeads on Lead (After update) {

    Map<ID, Integer> leadChange = new Map<ID, Integer>();

    Map<ID, Set<Lead>> routerQueue = new Map<ID, Set<Lead>>();

    Set<Lead> assignQueue = new Set<Lead>();

    for (Lead lead : Trigger.new) {
                
        String newOwnerId = lead.OwnerId;
        System.debug(newOwnerId);
        String oldOwnerId = Trigger.oldMap.get(lead.Id).OwnerId;
        System.debug(oldOwnerId);
        
        if(LeadUtil.getRTR(lead) && LeadUtil.isInQueue(lead) && LeadAssignUtil.firstTime == True){
            System.debug(lead.Name + 'Qualifies');
            User rep = [SELECT Id FROM User WHERE Id = :lead.repGrp__c];
            if(LeadUtil.routeBy(lead) == 'Route Randomly'){
                MapUtil.mapUpdate(routerQueue, rep.Id, lead, 'add');
                MapUtil.mapUpdate(leadChange, rep.Id, 1, '$inc');
            }
            else if(LeadUtil.routeBy(lead) == 'Use Default Assignment Rule'){
                assignQueue.add(lead);
            }
            else if(LeadUtil.routeBy(lead) == 'Assign to Campaign Owner'){
                LeadUtil.changeOwner(lead, LeadUtil.campaignOwner(lead));
                MapUtil.mapUpdate(leadChange, LeadUtil.campaignOwner(lead), 1, '$inc');
            }
        }
        else if((LeadUtil.mvRepToRep(oldOwnerId, newOwnerId) || LeadUtil.mvToQueue(oldOwnerId, newOwnerId) || Lead.ISConverted) && LeadAssignUtil.firstTime == True){
            String name = lead.Name;
            System.debug(lead.name + 'Qualifies');
            MapUtil.mapUpdate(leadChange, Trigger.oldMap.get(lead.Id).OwnerId, -1, '$inc');
        }
        else{
            System.debug(lead.Name + 'does not qualify');
        }
    }

    if (leadChange.keySet().isEmpty() == false){
        
        for (ID idVal : leadChange.keySet()) {
            User owner = [SELECT Id, Routing__c FROM User WHERE Id = :idVal];
            if (owner.Routing__c == 'Route Randomly'){
                MapUtil.mapUpdate(routerQueue, idVal, OwnerUtil.getLeads(leadChange, idVal), 'add');
            }
        }
         
        System.debug('routerQueue: ' + routerQueue + ' assignQueue: ' + assignQueue);
        
        Set<Lead> newLeads = new Set<Lead>();
        for (Lead aLead : LeadUtil.changeOwner(routerQueue)){
            newLeads.add(aLead);
        }
        for (Lead aLead : LeadUtil.runRules(assignQueue)){
            newLeads.add(aLead);
        }
        
        List<Lead> newLeadsList = new List<Lead>();
        
        for (Lead aLead : newLeads){
            newLeadsList.add(aLead);
        }

        LeadAssignUtil.firstTime = false;
            
        Database.Update(newLeadsList);

    }
        
}