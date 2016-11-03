package de.hahn.blog.view.beans;

import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;

import oracle.adf.share.logging.ADFLogger;
import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.context.AdfFacesContext;
import oracle.adf.view.rich.event.QueryEvent;
import oracle.adf.view.rich.model.AttributeCriterion;
import oracle.adf.view.rich.model.ConjunctionCriterion;
import oracle.adf.view.rich.model.Criterion;
import oracle.adf.view.rich.model.FilterableQueryDescriptor;


public class ResetTableFilterBean {
    private static ADFLogger logger = ADFLogger.createADFLogger(ResetTableFilterBean.class);
    private RichTable empTable;
    private String shortDescription;

    public ResetTableFilterBean() {
    }

    public void resetTableFilter(ActionEvent actionEvent) {
        FilterableQueryDescriptor queryDescriptor = (FilterableQueryDescriptor) getEmpTable().getFilterModel();
        if (queryDescriptor != null && queryDescriptor.getFilterCriteria() != null) {
            queryDescriptor.getFilterCriteria().clear();
            getEmpTable().queueEvent(new QueryEvent(getEmpTable(), queryDescriptor));
            // PPR refresh a jsf component
            //            AdfFacesContext.getCurrentInstance().addPartialTarget(getEmpTable());

        }
    }

    /**
     * method to reset filter attributes on an af:table
     * @param actionEvent event which triggers the method
     */
    public void resetTableFilter12c(ActionEvent actionEvent) {
        FilterableQueryDescriptor queryDescriptor = (FilterableQueryDescriptor) getEmpTable().getFilterModel();
        if (queryDescriptor != null && queryDescriptor.getFilterConjunctionCriterion() != null) {
            logger.info("Filter found...");
            ConjunctionCriterion cc = queryDescriptor.getFilterConjunctionCriterion();
            List<Criterion> lc = cc.getCriterionList();
            if (!lc.isEmpty()){
                logger.info("...iterating criterions...");
            }
            for (Criterion c : lc) {
                if (c instanceof AttributeCriterion) {
                    AttributeCriterion ac = (AttributeCriterion) c;
                    Object object = ac.getValue();
                    logger.info("...found " + ac.getAttribute().getName() + " value: " + object);
                    if (object != null) {
                        ac.setValue(null);
                        logger.info("...reset...");
                    }
                }
            }
            getEmpTable().queueEvent(new QueryEvent(getEmpTable(), queryDescriptor));
        }
    }

    public void setEmpTable(RichTable empTable) {
        this.empTable = empTable;
    }

    public RichTable getEmpTable() {
        return empTable;
    }


    public void setShortDescription(String shortDescritopn) {
        logger.info("Set ShortDescription called");
        this.shortDescription = shortDescritopn;
    }

    public String getShortDescription() {
        logger.info("get ShortDescription called");
        AdfFacesContext adfFacesCtx = AdfFacesContext.getCurrentInstance();

        // get the PageFlowScope Params
        Map<String, Object> scopePageFlowScopeVar = adfFacesCtx.getPageFlowScope();
        Boolean reset = (Boolean) scopePageFlowScopeVar.getOrDefault("resetFilter", Boolean.FALSE);
        boolean flip = reset.booleanValue();
        if (flip) {
            logger.info("ResetTable Filter!");
            resetTableFilter12c(null);
            scopePageFlowScopeVar.put("resetFilter", Boolean.FALSE);
            logger.info("Unset filter reset flag!");

        }

        return shortDescription;
    }

    public void setRestFlag() {
        AdfFacesContext adfFacesCtx = AdfFacesContext.getCurrentInstance();
        // get the PageFlowScope Params
        Map<String, Object> scopePageFlowScopeVar = adfFacesCtx.getPageFlowScope();
        scopePageFlowScopeVar.put("resetFilter", Boolean.TRUE);
        logger.info("Set filter reset flag!");
    }
}
