package nc.bs.ps.report.report4551;

import nc.vo.ml.NCLangRes4VoTransl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import javax.swing.tree.TreeNode;
import nc.bs.logging.Logger;
import nc.bs.ps.report.util.PSReportPubOpreate;
import nc.impl.ps.pub.PSAbstractReportQryImp;
import nc.impl.ps.pub.ReportDMO;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.querytemplate.filter.IFilter;
import nc.ui.querytemplate.meta.IFilterMeta;
import nc.ui.querytemplate.querytree.QueryTree;
import nc.ui.querytemplate.querytree.QueryTree.FilterNode;
import nc.ui.querytemplate.querytree.QueryTree.QueryTreeNode;
import nc.ui.querytemplate.value.IFieldValue;
import nc.ui.querytemplate.value.RefValueObject;
import nc.vo.crmbd.prrevfare.CrRevfareVO;
import nc.vo.crmbd.pub.tools.CommonUtil;
import nc.vo.jzfdc.pub.report.ReportBaseVO;
import nc.vo.jzfdc.pub.report.ReportQryTool;
import nc.vo.ps.pub.bd.DbTempTableDMO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.lang.UFDate;
import nc.vo.trade.pub.IBillStatus;
import org.apache.commons.collections.Predicate;

public class ReportQueryImpl extends PSAbstractReportQryImp{


	public static final String COND_PROJECT = "crm_bd_house.pk_project";
	public static final String COND_SITUATION = "crm_bd_house.pk_situation";
	public static final String COND_CUSTOMER = "ps_cb_gathering_c.pk_customer";
	public static final String COND_HOUSE = "crm_bd_house.pk_house";
	public static final String COND_DATE_BEGIN = "ps_cb_gathering.dchargedate1";
	public static final String COND_DATE_END = "ps_cb_gathering.dchargedate2";
	public static final String COND_DATE= "ps_cb_gathering_b.dchargedate";
	public static final String COND_FUNDSET = "ps_cb_gathering_b.pk_fundset";
	public static final String COND_BANK_IN = "ps_cb_gathering.pk_bank_in";
	public static final String COND_BANK_ID = "ps_cb_gathering.pk_accid";
	public static final String COND_STATUS = "ps_cb_gathering.fgatherstatus";
	public static final String COND_BISDMLTN = "bisdmltn";
	public static final String COND_BISDMLTN_SIGN = "ps_so_sign.bisdmltn";
	public static final String COND_BISDMLTN_SUBSC = "ps_so_subsc.bisdmltn";
	public static final String COND_PAYMODE = "pk_paymode";
	public static final String COND_PAYMODE_SIGN = "ps_so_sign.pk_paymode";
	public static final String COND_PAYMODE_SUBSC = "ps_so_subsc.pk_paymode";
	public static final String PROJECTNAME = "project";
	public static final String SITUATION = "situation";

	@Override
	public String[] getOrderColNames() {
		// TODO Auto-generated method stub!
		return null;
	}

	@Override
	public String getQuerySql(QueryTree currTree, QueryTree powerTree,
			Object userObject) {
		return getWhereSql(currTree, powerTree, userObject);
	}

	@SuppressWarnings("unused")
	private HashMap<String, String> parseCond(QueryTree currTree) throws Exception {
		HashMap<String, String> retMap = new HashMap<String, String>();

		if (currTree != null) {
			QueryTree tree = currTree.trimLeafNodes(new Predicate() {
				public boolean evaluate(Object object) {
					if (object instanceof FilterNode) {
						IFilter filter = (IFilter) ((FilterNode) object).getUserObject();
						return !filter.isValidate();
					}
					return false;
				}

			});

			TreeNode copyroot = (TreeNode) tree.getRoot();
			if (copyroot.getChildCount() > 0) {
				QueryTreeNode realRoot = (QueryTreeNode) copyroot.getChildAt(0);
				for (int i = 0; i < realRoot.getChildCount(); i++) {
					QueryTreeNode child = (QueryTreeNode) realRoot.getChildAt(i);
					if (child == null
							|| child.getUserObject() == null
							|| ((IFilter) child.getUserObject()).getFilterMeta() == null
							|| ((IFilter) child.getUserObject()).getFieldValue() == null
							|| ((IFieldValue) ((IFilter) child.getUserObject()).getFieldValue()).getFieldValues() == null
							|| ((IFieldValue) ((IFilter) child.getUserObject()).getFieldValue()).getFieldValues()
									.get(0) == null) {
						return null;
					}
					String name = ((IFilterMeta) ((IFilter) child.getUserObject()).getFilterMeta()).getFieldCode();
					Object value = ((IFieldValue) ((IFilter) child.getUserObject()).getFieldValue()).getFieldValues()
							.get(0).getValueObject();
					if (COND_PROJECT.equalsIgnoreCase(name)) {
						String strCondProjectPK = ((RefValueObject) value).getPk();
						// 取得多选数组的长度
						int listsize = ((IFieldValue) ((IFilter) child.getUserObject()).getFieldValue())
								.getFieldValues().size();
						String projWherePart = " ";
						// 取得第一个项目对应的wheresql
						projWherePart = new PSReportPubOpreate()
								.getChildProjPKByWherePart(((RefValueObject) (((IFieldValue) ((IFilter) child
										.getUserObject()).getFieldValue()).getFieldValues().get(0).getValueObject()))
										.getPk());
						// 遍历剩余项目，并在项目前面加,以关联
						for (int k = 1; k < listsize; k++) {
							String pk_project = ((RefValueObject) (((IFieldValue) ((IFilter) child.getUserObject())
									.getFieldValue()).getFieldValues().get(k).getValueObject())).getPk();
							projWherePart += "," + new PSReportPubOpreate().getChildProjPKByWherePart(pk_project);
						}
						retMap.put(COND_PROJECT, projWherePart);
					} else if (COND_DATE_BEGIN.equalsIgnoreCase(name)) {
						retMap.put(COND_DATE_BEGIN, ((UFDate) value).toString());
					} else if (COND_DATE_END.equalsIgnoreCase(name)) {
						retMap.put(COND_DATE_END, ((UFDate) value).toString());
					} else if (COND_SITUATION.equalsIgnoreCase(name)) {
						retMap.put(COND_SITUATION, (value).toString());
					}else if (COND_CUSTOMER.equalsIgnoreCase(name)) {
						retMap.put(COND_CUSTOMER, ( value).toString());
					}else if (COND_HOUSE.equalsIgnoreCase(name)) {
						retMap.put(COND_HOUSE, ((RefValueObject)value).getPk());
					}else if (COND_FUNDSET.equalsIgnoreCase(name)) {
						retMap.put(COND_FUNDSET, ( value).toString());
					}else if (COND_BANK_IN.equalsIgnoreCase(name)) {
						retMap.put(COND_BANK_IN, (value).toString());
					}else if (COND_BANK_ID.equalsIgnoreCase(name)) {
						retMap.put(COND_BANK_ID, ( value).toString());
					}else if (COND_STATUS.equalsIgnoreCase(name)) {
						retMap.put(COND_STATUS, ((DefaultConstEnum)value).getValue().toString());
					}
				}
			}
		}
		return retMap;
	}


	public ReportBaseVO[] queryDataByCond(QueryTree currTree,
			QueryTree powerTree, Object userObj) throws BusinessException {
		ReportBaseVO[] returnVOs = null;
//		HashMap<String, String> condMap = null;
		try {
			sql = getWhereSql(currTree, powerTree, userObj);
			dealDefaultSqlProjectAndSituation(currTree);
//			condMap = parseCond(currTree);
			returnVOs = getSQL();
		}catch(BusinessException busiEx){
			throw busiEx;
		} catch (Exception e) {
			Logger.error("数据查询失败", e);
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000851")//@res "数据查询失败!"
, e);
		}

		return returnVOs;
	}

	/**
	 * <p>功能描述:处理查询框中得到的sql中pk_project 、 pk_situation、dchargedate</p>
	 * <p>创建人及时间： chenhf 2011-7-22下午02:39:10</p>
	 * @param currTree
	 * @throws BusinessException
	 */
	private void dealDefaultSqlProjectAndSituation(QueryTree currTree) throws BusinessException{
		if (currTree != null) {
			QueryTree tree = currTree.trimLeafNodes(new Predicate() {
				public boolean evaluate(Object object) {
					if (object instanceof FilterNode) {
						IFilter filter = (IFilter) ((FilterNode) object).getUserObject();
						return !filter.isValidate();
					}
					return false;
				}

			});

			TreeNode copyroot = (TreeNode) tree.getRoot();
			if (copyroot.getChildCount() > 0) {
				QueryTreeNode realRoot = (QueryTreeNode) copyroot.getChildAt(0);
				if(realRoot!=null && !"".equals(realRoot)&& realRoot.getChildCount()==0){
					//查询条件只选择项目的情况下
					if (realRoot == null
							|| realRoot.getUserObject() == null
							|| ((IFilter) realRoot.getUserObject()).getFilterMeta() == null
							|| ((IFilter) realRoot.getUserObject()).getFieldValue() == null
							|| ((IFieldValue) ((IFilter) realRoot.getUserObject()).getFieldValue()).getFieldValues() == null
							|| ((IFieldValue) ((IFilter) realRoot.getUserObject()).getFieldValue()).getFieldValues()
									.get(0) == null) {
						return ;
					}
					String name = ((IFilterMeta) ((IFilter) realRoot.getUserObject()).getFilterMeta()).getFieldCode();
					if (COND_PROJECT.equalsIgnoreCase(name)) {
						// 取得多选数组的长度
						String[] projectArray = getFieldArray(realRoot);
						dealProjectSql(currTree, projectArray);
					}
				}
				for (int i = 0; i < realRoot.getChildCount(); i++) {
					QueryTreeNode child = (QueryTreeNode) realRoot.getChildAt(i);
					if (child == null
							|| child.getUserObject() == null
							|| ((IFilter) child.getUserObject()).getFilterMeta() == null
							|| ((IFilter) child.getUserObject()).getFieldValue() == null
							|| ((IFieldValue) ((IFilter) child.getUserObject()).getFieldValue()).getFieldValues() == null
							|| ((IFieldValue) ((IFilter) child.getUserObject()).getFieldValue()).getFieldValues()
									.get(0) == null) {
						return ;
					}
					String name = ((IFilterMeta) ((IFilter) child.getUserObject()).getFilterMeta()).getFieldCode();
					if (COND_PROJECT.equalsIgnoreCase(name)) {
						// 取得多选数组的长度
						String[] projectArray = getFieldArray(child);
						dealProjectSql(currTree, projectArray);
					}else if(COND_SITUATION.equals(name)){
						dealSituationSql(currTree, child);
					}else if(COND_BISDMLTN.equals(name)){
						dealIsdmltn(currTree, child);
					}else if(COND_DATE_BEGIN.equals(name)){
						sql = sql.replaceAll(COND_DATE_BEGIN, COND_DATE);
					}else if(COND_DATE_END.equals(name)){
						sql = sql.replaceAll(COND_DATE_END, COND_DATE);
					}else if(COND_STATUS.equals(name)){
						String statusSql = ReportQryTool.getQryTreeNodeWhereSql(currTree, COND_STATUS);
						String temp = "";
						if(statusSql!=null && statusSql.length()>0 && statusSql.indexOf("0")>=0){
							temp = "(" + statusSql +" or ps_cb_gathering.fgatherstatus is null " + ")";
							sql = sql.replace(statusSql,temp);
						}
					}
				}
			}
		}
	}
	/**
	 * <p>功能描述:处理是否拆迁字段逻辑值</p>
	 * <p>创建人及时间： chenhf 2011-7-22下午08:11:19</p>
	 * @param currTree
	 * @param child
	 */
	private void dealIsdmltn(QueryTree currTree, QueryTreeNode child) {
		int listsize = ((IFieldValue) ((IFilter) child.getUserObject()).getFieldValue()).getFieldValues().size();
		if(listsize>0){
			String pk_situationDefSql = ReportQryTool.getQryTreeNodeWhereSql(currTree, COND_BISDMLTN);
			String pk_situationSql = pk_situationDefSql;
			pk_situationSql = pk_situationSql.replace("0", "'Y'").replace("1", "'N'");
			if(pk_situationSql!=null && pk_situationSql.length()>0){
				sql = sql.replaceAll(pk_situationDefSql,pk_situationSql) ;
			}
		}
	}

	private void dealSituationSql(QueryTree currTree, QueryTreeNode child)
			throws BusinessException {
		String[] situationArray = getFieldArray(child);
		if(situationArray!=null && situationArray.length>0){
			try {
				String situationWherePart = " crm_bd_house.pk_situation in (" + new PSReportPubOpreate().getChildSituationBySituationArray(situationArray)
								+ " )";
				String pk_situationSql = ReportQryTool.getQryTreeNodeWhereSql(currTree, COND_SITUATION);
				if(pk_situationSql!=null && pk_situationSql.length()>0){
					sql = sql.replaceAll(pk_situationSql,situationWherePart) ;
				}
			} catch (Exception e) {
				Logger.error("获取业态出错", e);
				throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("HYPS","UPPHYPS-003308")//@res "获取业态出错"
, e);
			}
		}
	}

	private void dealProjectSql(QueryTree currTree, String[] projectArray)
			throws BusinessException {
		if(projectArray!=null &&projectArray.length>0){
			try {
				String projWherePart = "crm_bd_house.pk_project in (" + new PSReportPubOpreate().getChildProjPKByProjectArray(projectArray)
								+ " )";
				String pk_projectSql = ReportQryTool.getQryTreeNodeWhereSql(currTree, COND_PROJECT);
				if(pk_projectSql!=null && pk_projectSql.length()>0){
					sql = sql.replaceAll(Pattern.quote(pk_projectSql),projWherePart) ;
				}

			} catch (Exception e) {
				Logger.error("获取项目主键出错", e);
				throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("HYPS","UPPHYPS-003309")//@res "获取项目出错"
, e);
			}
		}
	}

	private String[] getFieldArray(QueryTreeNode child) {
		if(child==null){
			return null;
		}
		int listsize = ((IFieldValue) ((IFilter) child.getUserObject()).getFieldValue())
				.getFieldValues().size();
		String[] projectArray = new String[listsize];
		// 遍历项目
		for (int k = 0; k < listsize; k++) {
			String pk_project = ((RefValueObject) (((IFieldValue) ((IFilter) child.getUserObject())
					.getFieldValue()).getFieldValues().get(k).getValueObject())).getPk();
			projectArray[k] = pk_project;
		}
		return projectArray;
	}


	private ReportBaseVO[] getSQL() throws Exception {
		// 挞定金和违约金和手续费用
		ReportBaseVO[] bodyAllVOs = null;
//		sql = " and 1=1 ";
//		customer = null;
		if(sql!=null && sql.length()>0){
//		if (condMap != null && condMap.size() > 0) {
//
//			if (condMap.get(COND_PROJECT)!=null && condMap.get(COND_PROJECT).length()>0) {
//				sql = sql + " and crm_bd_house.pk_project in(" + condMap.get(COND_PROJECT)
//						+ ")";
//			}
//			if (condMap.get(COND_DATE_BEGIN)!=null && condMap.get(COND_DATE_BEGIN).length()>0) {
//				sql = sql + " and ps_cb_gathering_b.dchargedate>='" + condMap.get(COND_DATE_BEGIN) + "'";
//			}
//			if (condMap.get(COND_DATE_END)!=null && condMap.get(COND_DATE_END).length()>0) {
//				sql = sql + " and ps_cb_gathering_b.dchargedate<='" + condMap.get(COND_DATE_END) + "'";
//				// timeSql = " '" + Condition[i].getValue() + "'";
//
//			}
//			if (condMap.get(COND_SITUATION)!=null && condMap.get(COND_SITUATION).length()>0) {
//				sql = sql + " and crm_bd_house.pk_situation='" + condMap.get(COND_SITUATION) + "'";
//			}
//			if (condMap.get(COND_HOUSE)!=null && condMap.get(COND_HOUSE).length()>0) {
//				sql = sql + " and ps_cb_gathering.pk_house='" + condMap.get(COND_HOUSE) + "'";
//			}
//			if (condMap.get(COND_FUNDSET)!=null && condMap.get(COND_FUNDSET).length()>0) {
//				sql = sql + " and ps_cb_gathering_b.PK_FUNDSET='" + condMap.get(COND_FUNDSET) + "'";
//			}
//			if (condMap.get(COND_CUSTOMER)!=null && condMap.get(COND_CUSTOMER).length()>0) {
//				customer = "'" + condMap.get(COND_CUSTOMER) + "'";
//				// sql=sql+" and ps_cb_gathering_c.pk_customer='" +
//				// Condition[i].getValue() +"'" ;
//			}
//			if (condMap.get(COND_BANK_IN)!=null && condMap.get(COND_BANK_IN).length()>0) {
//				sql = sql + " and ps_cb_gathering_b.pk_bank_in='" +  condMap.get(COND_BANK_IN) + "'";
//			}
//			if (condMap.get(COND_BANK_ID)!=null && condMap.get(COND_BANK_ID).length()>0) {
//				sql = sql + " and ps_cb_gathering_b.pk_accid='" + condMap.get(COND_BANK_ID) + "'";
//			}
//			if (condMap.get(COND_STATUS)!=null && condMap.get(COND_STATUS).length()>0) {
//				sql = sql + " and ps_cb_gathering.fgatherstatus=" + condMap.get(COND_STATUS) + "";
//			}

		ReportDMO reportDMO = new ReportDMO();;
		ReportBaseVO[] signVOs =null;
		ReportBaseVO[] subscVOs=null;
		ReportBaseVO[] subscsignVOs=null;
		ReportBaseVO[] unSubSignVOs=null;

		try {
			signVOs = reportDMO.queryVOBySql(signSQL());
			subscVOs = reportDMO.queryVOBySql(subscSQL());
			subscsignVOs = reportDMO.queryVOBySql(subscsignSQL());
			if(sql.indexOf(COND_BISDMLTN)<0){
				unSubSignVOs = reportDMO.queryVOBySql(unSubscSQL());
			}
		} catch (Exception e) {
			if(e instanceof BusinessException)
				throw e;
			else{
				Logger.error(e.getStackTrace(), e);
				throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-002270")//@res "后台查询错误"
);}
		}


		Map<String, List<ReportBaseVO>> project_reportList_map = new HashMap<String, List<ReportBaseVO>>();
		if (signVOs != null && signVOs.length > 0) {
			Map<String, ReportBaseVO> signmap = new HashMap<String, ReportBaseVO>();
			getVoMap(signVOs, signmap);
			setComboValueOfSign(signVOs);
			// 遍历HashMap,将数据放入ArrayList中
			Iterator it1 = signmap.keySet().iterator();
			while (it1.hasNext()) {
//				allVoList.add(signmap.get(key));
				String key = (String)it1.next();
				String pk_project = (String)signmap.get(key).getAttributeValue(PROJECTNAME);
//				allVoList.add(signmap.get(key));
				if(project_reportList_map.containsKey(pk_project)){
					project_reportList_map.get(pk_project).add(signmap.get(key));
				}else {
					List<ReportBaseVO> list = new ArrayList<ReportBaseVO>();
					list.add(signmap.get(key));
					project_reportList_map.put(pk_project, list);
				}
			}
		}
		if (subscVOs != null && subscVOs.length > 0) {
			Map<String, ReportBaseVO> signmap = new HashMap<String, ReportBaseVO>();
			getVoMap(subscVOs, signmap);
			Iterator it1 = signmap.keySet().iterator();
			while (it1.hasNext()) {
//				allVoList.add(signmap.get(it1.next()));
				String key = (String)it1.next();
				String pk_project = (String)signmap.get(key).getAttributeValue(PROJECTNAME);
//				allVoList.add(signmap.get(key));
				if(project_reportList_map.containsKey(pk_project)){
					project_reportList_map.get(pk_project).add(signmap.get(key));
				}else {
					List<ReportBaseVO> list = new ArrayList<ReportBaseVO>();
					list.add(signmap.get(key));
					project_reportList_map.put(pk_project, list);
				}

			}
		}
		if (subscsignVOs != null && subscsignVOs.length > 0) {
			Map<String, ReportBaseVO> signmap = new HashMap<String, ReportBaseVO>();
			getVoMap(subscsignVOs, signmap);
			setComboValueOfSubscsign(subscsignVOs);
			Iterator it1 = signmap.keySet().iterator();
			while (it1.hasNext()) {
//				allVoList.add(signmap.get(it1.next()));
				String key = (String)it1.next();
				String pk_project = (String)signmap.get(key).getAttributeValue(PROJECTNAME);
//				allVoList.add(signmap.get(key));
				if(project_reportList_map.containsKey(pk_project)){
					project_reportList_map.get(pk_project).add(signmap.get(key));
				}else {
					List<ReportBaseVO> list = new ArrayList<ReportBaseVO>();
					list.add(signmap.get(key));
					project_reportList_map.put(pk_project, list);
				}
			}
		}
		if (unSubSignVOs != null && unSubSignVOs.length > 0) {
			Map<String, ReportBaseVO> signmap = new HashMap<String, ReportBaseVO>();
			getVoMap(unSubSignVOs, signmap);
			Iterator it1 = signmap.keySet().iterator();
			while (it1.hasNext()) {
//				allVoList.add(signmap.get(it1.next()));
				String key = (String)it1.next();
				String pk_project = (String)signmap.get(key).getAttributeValue(PROJECTNAME);
//				allVoList.add(signmap.get(key));
				if(project_reportList_map.containsKey(pk_project)){
					project_reportList_map.get(pk_project).add(signmap.get(key));
				}else {
					List<ReportBaseVO> list = new ArrayList<ReportBaseVO>();
					list.add(signmap.get(key));
					project_reportList_map.put(pk_project, list);
				}
			}
		}
		if(project_reportList_map!=null && project_reportList_map.keySet()!=null){
			ArrayList<ReportBaseVO> allVoList = new ArrayList<ReportBaseVO>();
			for(String key : project_reportList_map.keySet()){
				List<ReportBaseVO> list = project_reportList_map.get(key);
				if(list!=null && list.size()>0)
				for(ReportBaseVO reportVO:list){
					if(reportVO!=null){
						allVoList.add(reportVO);
					}
				}
			}

			//转换换票票据号
			Set<String> custSet = new HashSet<String>();
			for (ReportBaseVO vo : allVoList) {
				if (vo == null || CommonUtil.isNullStr(vo.getAttributeValue(CrRevfareVO.PK_CHGBILLLIST)))
					continue;
				custSet.addAll(splidCusts(vo));
			}
			if (custSet.contains(null)) {
				custSet.remove(null);
			}
			String changeBillSql = "";
			try{
				DbTempTableDMO tempDMO = new DbTempTableDMO();
				changeBillSql = tempDMO.insertTempTable(custSet.toArray(new String[0]), "tempTalbe", "billPk");
			}catch(Exception e){

			}
			StringBuffer sb = new StringBuffer();
			sb.append(" select  pk_billlist,vbmnum ")
			  .append(" from crm_bm_billlist ")
			  .append(" where pk_billlist in ").append(changeBillSql)
			  .append(" and isnull(dr, 0) = 0");
			ReportBaseVO[] changeBillVOs = reportDMO.queryVOBySql(sb.toString());
			Map<String, String> pkBill_num_map = new HashMap<String, String>();
			if(changeBillVOs!=null && changeBillVOs.length>0){
				for(ReportBaseVO tempVO:changeBillVOs){
					if(tempVO!=null && tempVO.getAttributeValue("pk_billlist")!=null && tempVO.getAttributeValue("vbmnum")!=null){
						if(!pkBill_num_map.containsKey(tempVO.getAttributeValue("pk_billlist"))){
							pkBill_num_map.put((String)tempVO.getAttributeValue("pk_billlist"), (String)tempVO.getAttributeValue("vbmnum"));
						}
					}
				}
				for(ReportBaseVO tmpVO:allVoList){
					String pk_chgbilllist = (String)tmpVO.getAttributeValue("pk_chgbilllist");
					String temp = "";
					if(pk_chgbilllist!=null && !"".equals(pk_chgbilllist)){
						String[] chgBillArray = pk_chgbilllist.split(",");
						if(chgBillArray.length>0){
							for(int i=0;i<chgBillArray.length-1;i++){
								if(chgBillArray[i]!=null && !"".equals(chgBillArray[i].trim())){
									temp += pkBill_num_map.get(chgBillArray[i].trim()) + ",";
								}
							}
							if(chgBillArray[chgBillArray.length-1]!=null && !"".equals(chgBillArray[chgBillArray.length-1].trim())){
								temp += pkBill_num_map.get(chgBillArray[chgBillArray.length-1].trim());
							}
							tmpVO.setAttributeValue("pk_chgbilllist", temp);
						}
					}
				}
			}

			ReportBaseVO[] newAllVOs = new ReportBaseVO[allVoList.size()];
			allVoList.toArray(newAllVOs);
	//		bodyAllVOs = bodyModelVO(newAllVOs);
			// VOUtil.ascSort((nc.vo.pub.CircularlyAccessibleValueObject[])bodyAllVOs,
			// new String[]{"pk_customer"});
			return newAllVOs;
		}
		}
//		return bodyAllVOs;
		return null;
	}


	public ReportBaseVO[] bodyModelVO(ReportBaseVO[] baseVO) {
		HashMap<String, ArrayList<ReportBaseVO>> curMap = new HashMap<String, ArrayList<ReportBaseVO>>();
		ArrayList<ReportBaseVO> curList = null;
		int k = 0;
		Object proObj = null;
		for (ReportBaseVO tmpVO : baseVO) {
			if (curMap.containsKey(tmpVO.getAttributeValue(SITUATION).toString())) {
				curMap.get(tmpVO.getAttributeValue(SITUATION).toString()).add(tmpVO);
			} else {
				curList = new ArrayList<ReportBaseVO>();
				if (k == 0) {
					proObj = tmpVO.getAttributeValue(PROJECTNAME);
					tmpVO.setAttributeValue(PROJECTNAME, tmpVO.getAttributeValue(PROJECTNAME));
					k++;
				}
				curList.add(tmpVO);
				curMap.put(tmpVO.getAttributeValue(SITUATION).toString(), curList);

			}
		}
		curList = new ArrayList<ReportBaseVO>();
		ReportBaseVO curVO = null;
		for (String strPK : curMap.keySet()) {
			curVO = new ReportBaseVO();
			curVO.setAttributeValue(SITUATION, strPK);
			curVO.setAttributeValue(PROJECTNAME, proObj);
			curList.add(curVO);
			for (ReportBaseVO tmpVO : curMap.get(strPK)) {
				tmpVO.setAttributeValue(SITUATION, "");
				tmpVO.setAttributeValue(PROJECTNAME, "");
				curList.add(tmpVO);
			}
		}
		ReportBaseVO[] retVO = new ReportBaseVO[curList.size()];
		for (int i = 0; i < curList.size(); i++) {
			retVO[i] = curList.get(i);

		}
		return retVO;
	}


	/**
	 * <p>修改描述:注释掉历史置业顾问信息</p>
	 * <p>修改人及时间： chenhf 2011-7-22下午04:02:59</p>
	 * @param signVOs
	 * @param signmap
	 */
	private void getVoMap(ReportBaseVO[] signVOs, Map<String, ReportBaseVO> signmap) {
		for (int i = 0; i < signVOs.length; i++) {
			String key = signVOs[i].getAttributeValue("pk_gathering").toString() + signVOs[i].getAttributeValue("pk_gathering_b").toString();
			if (signmap.get(key) == null) {
				// 如果HashMap中不存在该收款单，那么将此收款单增加进去
				if (signVOs[i].getAttributeValue("bcsellornow")!=null &&
						signVOs[i].getAttributeValue("bcsellornow").toString().equals("Y")) {
					signmap.put(key, signVOs[i]);
				} else if (signVOs[i].getAttributeValue("bcsellornow").toString().equals("N")) {
//					String oldString = signVOs[i].getAttributeValue("cussellor").toString();
					signVOs[i].setAttributeValue("cussellor", null);
//					signVOs[i].setAttributeValue("oldsellor", oldString);
					signmap.put(key, signVOs[i]);
				}
			} else {
				// 如果HaspMap中已经存在些收款单，那么将置业顾问相加
				if (signVOs[i].getAttributeValue("bcsellornow")!=null &&
						signVOs[i].getAttributeValue("bcsellornow").toString().equals("Y")) {
					// 如果是当前置业顾问
					String cussellor = signmap.get(key).getAttributeValue("cussellor") == null ? "" : signmap.get(key).getAttributeValue("cussellor")
							.toString();
					if (cussellor == null || cussellor.trim().length() == 0
							||(signVOs[i].getAttributeValue("cussellor")!=null&&cussellor.contains((String)signVOs[i].getAttributeValue("cussellor"))))
						cussellor = signVOs[i].getAttributeValue("cussellor").toString();
					else
						cussellor = cussellor + "," + signVOs[i].getAttributeValue("cussellor").toString();
					signmap.get(key).setAttributeValue("cussellor", cussellor);
//				} else if (signVOs[i].getAttributeValue("bcsellornow")==null ||
//						signVOs[i].getAttributeValue("bcsellornow").toString().equals("N")) {
					// 如果是历史置业顾问
					// 取得Hashmap中的原有的置业顾问
//					String oldsellor = signmap.get(key).getAttributeValue("oldsellor") == null ? "" : signmap.get(key).getAttributeValue("oldsellor")
//							.toString();
					// 如果此vo中没有置业顾问
//					if (signVOs[i].getAttributeValue("cussellor") == null) {
//						signmap.get(key).setAttributeValue("oldsellor", oldsellor);
//					} else {
//						if(oldsellor==null||oldsellor.trim().length()==0)
//							oldsellor = signVOs[i].getAttributeValue("cussellor").toString();
//						else
//							oldsellor = oldsellor + "," + signVOs[i].getAttributeValue("cussellor").toString();
//						signmap.get(key).setAttributeValue("oldsellor", oldsellor);
//					}
				}
			}
		}
	}


	private String sql = null;

//	private String customer = null;

	private String signSQL() {
		String whereSql = sql;
		whereSql = whereSql.replaceAll(COND_BISDMLTN, COND_BISDMLTN_SIGN).replaceAll(COND_PAYMODE, COND_PAYMODE_SIGN);
		StringBuffer strbuf = new StringBuffer();
		strbuf.append("select ");
		strbuf.append("ps_cb_gathering_b.pk_gathering,");
		strbuf.append("ps_cb_gathering_b.pk_gathering_b,");
		strbuf.append("ps_cb_gathering.reserve1,");//add by zhaohf time 2012-03-22  增加交款人
		strbuf.append("fdc_bd_project.vname as project,");
		strbuf.append("fdc_bd_operstate.vname as situation,");
		strbuf.append("ps_cb_gathering.vcustomers as pk_customer,");
		strbuf.append("crm_bd_house.pk_sign ,");
		strbuf.append("ps_cb_gathering_b.nbthisaccountreceiv as shouldmny,");
		strbuf.append("ps_cb_gathering.pk_house,");
		strbuf.append("ps_bd_fundset.vfsname as fundset_name,");
		strbuf.append("isnull(ps_cb_gathering_b.nbrthisaccountreceiv,0) as mny,");
		strbuf.append("gatheringpaymode.balanname as paymode_name ,");
		strbuf.append("ps_so_sign.nsignarea as nsellarea,");
		strbuf.append("ps_cb_gathering.fgatherstatus,");
		strbuf.append("ps_so_sign.ntotalmnysign as nsellmny,");
		strbuf.append("crm_bd_house.vhname as house_name,");
		strbuf.append("crm_bd_house.nsellarea as nfinalarea,");
		strbuf.append("isnull(crm_bd_house.nbalancemny,ps_so_sign.ntotalmnysign) as nfinalmny,");
		strbuf.append("ps_cb_gathering.vbillno,");
		strbuf.append("ps_cb_gathering_b.vchargeno,");
		strbuf.append("ps_cb_gathering_b.dchargedate,");
		strbuf.append("ps_cb_gathering_b.fcardtype,");
		strbuf.append("ps_cb_gathering_b.fcardtype as fcardtype,");
		strbuf.append("ps_cb_gathering_b.vcardno,");
		strbuf.append("bankin.bankdocname as pk_bank_in,");
		strbuf.append("bd_bankaccbas.accountname as pk_accid,");
		strbuf.append("ps_cb_gathering_b.vmark,");
		strbuf.append("ps_cb_gathering.fgatherstatus as fgatherstatus,");
		strbuf.append("signpaymode.vpmname as pk_putmode,");
		strbuf.append("bd_psndoc.psnname as cussellor,");
		strbuf.append("ps_cmg_customer_sellor.bcsellornow ,");
		strbuf.append("ps_so_sign.bisdmltn as bisdmltn,");
		strbuf.append("ps_so_sign.bisdmltn as bisdmltn,");
		strbuf.append("bd_deptdoc.deptname as pk_deptdoc,");
		strbuf.append("psndoc.psnname as pk_psndoc_gather,");
		strbuf.append("ps_bd_fundtype.vftname as pk_fundtype,");
		strbuf.append("fundset.vfsname as voldfund ,");
		strbuf.append("bankout.bankdocname as pk_bank_out,");
		strbuf.append("ps_cb_gathering.fchgbilltype as fchgbilltype,");
		strbuf.append("ps_cb_gathering.pk_chgbilllist as pk_chgbilllist");
		strbuf.append(" from ps_cb_gathering ");
		strbuf.append(" inner join crm_bd_house on ps_cb_gathering.pk_house=crm_bd_house.pk_house");
		strbuf.append(" inner join ps_cb_gathering_b on ps_cb_gathering.pk_gathering=ps_cb_gathering_b.pk_gathering");
		strbuf.append(" inner join ps_cb_gathering_c on ps_cb_gathering.pk_gathering=ps_cb_gathering_c.pk_gathering");
		strbuf.append(" inner join ps_cmg_customer_sellor on PS_CMG_CUSTOMER_SELLOR.pk_customer=ps_cb_gathering_c.pk_customer");
		strbuf.append(" inner join bd_psndoc on ps_cmg_customer_sellor.pk_psndoc=bd_psndoc.pk_psndoc");
		strbuf.append(" inner join fdc_bd_project on fdc_bd_project.pk_project=crm_bd_house.pk_project");
		strbuf.append(" inner join fdc_bd_operstate on fdc_bd_operstate.pk_operstate=crm_bd_house.pk_situation");
		strbuf.append(" left join bd_psndoc psndoc on psndoc.pk_psndoc=ps_cb_gathering.pk_psndoc");
		strbuf.append(" left join ps_bd_fundset fundset on fundset.pk_fundset = ps_cb_gathering_b.voldfund");
		strbuf.append(" left outer join ps_so_sign on crm_bd_house.pk_sign=ps_so_sign.pk_sign");
		strbuf.append(" left outer join ps_bd_paymode signpaymode on signpaymode.pk_paymode=ps_so_sign.pk_paymode");
		strbuf.append(" left outer join bd_deptdoc on bd_deptdoc.pk_deptdoc=ps_cb_gathering.pk_deptdoc");
		strbuf.append(" left outer join ps_bd_fundtype on ps_bd_fundtype.pk_fundtype=ps_cb_gathering_b.pk_fundtype");
		strbuf.append(" left outer join ps_bd_fundset on ps_bd_fundset.pk_fundset=ps_cb_gathering_b.pk_fundset");	
		strbuf.append(" left outer join bd_balatype gatheringpaymode on gatheringpaymode.pk_balatype=ps_cb_gathering_b.pk_paymode");	
		strbuf.append(" left outer join bd_bankdoc bankout on bankout.pk_bankdoc=ps_cb_gathering_b.pk_bank_out");
		strbuf.append(" left outer join bd_bankdoc bankin on bankin.pk_bankdoc=ps_cb_gathering_b.pk_bank_in");
		strbuf.append(" left outer join bd_bankaccbas on bd_bankaccbas.pk_bankaccbas=ps_cb_gathering_b.pk_accid");
		strbuf.append(" where ps_cb_gathering_b.pk_fundset is not null");
		strbuf.append(" and crm_bd_house.pk_situation is not null");
		strbuf.append(" and isnull(ps_cb_gathering_b.dr,0)=0");
		strbuf.append(" and isnull(ps_cb_gathering.dr,0)=0");
		strbuf.append(" and isnull(crm_bd_house.dr,0)=0");
		strbuf.append(" and isnull(ps_so_sign.dr,0)=0");
		strbuf.append(" and (ps_so_sign.vlastbill is null or ps_so_sign.vlastbill!='946D')");
		strbuf.append(" and isnull(PS_CMG_CUSTOMER_SELLOR.dr,0)=0");
		strbuf.append(" and isnull(ps_so_sign.dr,0)=0");
		strbuf.append(" and ps_so_sign.vbillstatus='");
		strbuf.append( IBillStatus.CHECKPASS);
		strbuf.append("' " + whereSql);
		strbuf.append(" order by crm_bd_house.pk_project,crm_bd_house.pk_house,pk_customer ");
		return strbuf.toString();
	}

	/**
	 * 取认购并签约的数据。这些数据是签约之前收的钱。
	 *
	 * @时间：2008-4-14上午10:54:58
	 * @return
	 */
	private String subscsignSQL() {
		String whereSql = sql;
		StringBuffer strbuf = new StringBuffer();
		strbuf.append("select ");
		strbuf.append("ps_cb_gathering_b.pk_gathering ,");
		strbuf.append("ps_cb_gathering_b.pk_gathering_b,");
		strbuf.append("fdc_bd_project.vname as project,");
		strbuf.append("ps_cb_gathering.reserve1,");//add by zhaohf time 2012-03-22  增加交款人
		strbuf.append("fdc_bd_operstate.vname as situation,");
		strbuf.append("ps_cb_gathering.vcustomers as pk_customer,");
		strbuf.append("crm_bd_house.pk_subsc,");
		strbuf.append("ps_cb_gathering.pk_house,");
		strbuf.append("ps_bd_fundset.vfsname as fundset_name,");
		strbuf.append("isnull(ps_cb_gathering_b.nbrthisaccountreceiv,0) as mny,");
		strbuf.append("gatheringpaymode.balanname as paymode_name ,");
		strbuf.append("ps_cb_gathering_b.nbthisaccountreceiv as shouldmny,");
		strbuf.append("ps_so_subsc.nsalearea as nsellarea,");
		strbuf.append("ps_so_subsc.ntotalafterdis as nsellmny,");
		strbuf.append("crm_bd_house.vhname as house_name,");
		strbuf.append("crm_bd_house.nsellarea as nfinalarea,");
		strbuf.append("isnull(crm_bd_house.nbalancemny,ps_so_subsc.ntotalafterdis) as nfinalmny,");
		strbuf.append("ps_cb_gathering.vbillno,");
		strbuf.append("ps_cb_gathering.fgatherstatus,");
		strbuf.append("ps_cb_gathering_b.vchargeno,");
		strbuf.append("ps_cb_gathering_b.dchargedate,");
		strbuf.append("ps_cb_gathering_b.fcardtype,");
		strbuf.append("ps_cb_gathering_b.fcardtype as fcardtype,");
		strbuf.append("ps_cb_gathering_b.vcardno,");
		strbuf.append("bankin.bankdocname as pk_bank_in,");
		strbuf.append("bd_bankaccbas.accountname as pk_accid,");
		strbuf.append("ps_cb_gathering_b.vmark,");
		strbuf.append("ps_cb_gathering.fgatherstatus as fgatherstatus,");
		strbuf.append("signpaymode.vpmname as pk_putmode,");
		strbuf.append("bd_psndoc.psnname as cussellor,");
		strbuf.append("ps_cmg_customer_sellor.bcsellornow,");
		strbuf.append("ps_so_sign.bisdmltn as bisdmltn,");
		strbuf.append("bd_deptdoc.deptname as pk_deptdoc,");
		strbuf.append("psndoc.psnname as pk_psndoc_gather,");
		strbuf.append("ps_bd_fundtype.vftname as pk_fundtype,");
		strbuf.append("fundset.vfsname as voldfund ,");
		strbuf.append("bankout.bankdocname as pk_bank_out,");
		strbuf.append("ps_cb_gathering.fchgbilltype as fchgbilltype,");
		strbuf.append("ps_cb_gathering.pk_chgbilllist as pk_chgbilllist");
		strbuf.append(" from ps_cb_gathering");
		strbuf.append(" inner join crm_bd_house on ps_cb_gathering.pk_house=crm_bd_house.pk_house");
		strbuf.append(" inner join ps_cb_gathering_b on ps_cb_gathering.pk_gathering=ps_cb_gathering_b.pk_gathering");
		strbuf.append(" inner join ps_cb_gathering_c on ps_cb_gathering.pk_gathering=ps_cb_gathering_c.pk_gathering");
		strbuf.append(" inner join ps_cmg_customer_sellor on ps_cmg_customer_sellor.pk_customer=ps_cb_gathering_c.pk_customer");		
		strbuf.append(" inner join bd_psndoc on ps_cmg_customer_sellor.pk_psndoc=bd_psndoc.pk_psndoc");
		strbuf.append(" left join bd_psndoc psndoc on psndoc.pk_psndoc=ps_cb_gathering.pk_psndoc");
		strbuf.append(" left join ps_bd_fundset fundset on fundset.pk_fundset = ps_cb_gathering_b.voldfund");
		strbuf.append(" inner join fdc_bd_project on fdc_bd_project.pk_project=crm_bd_house.pk_project");
		strbuf.append(" inner join fdc_bd_operstate on fdc_bd_operstate.pk_operstate=crm_bd_house.pk_situation");
		strbuf.append(" left outer join ps_so_subsc on crm_bd_house.pk_subsc=ps_so_subsc.pk_subsc");
		strbuf.append(" left outer join ps_so_sign on ps_so_sign.pk_house=ps_so_subsc.pk_house");
		strbuf.append(" left outer join ps_bd_paymode signpaymode on signpaymode.pk_paymode=ps_so_sign.pk_paymode");
		strbuf.append(" left outer join bd_deptdoc on bd_deptdoc.pk_deptdoc=ps_cb_gathering.pk_deptdoc");
		strbuf.append(" left outer join ps_bd_fundtype on ps_bd_fundtype.pk_fundtype=ps_cb_gathering_b.pk_fundtype");
		strbuf.append(" left outer join ps_bd_fundset on ps_bd_fundset.pk_fundset=ps_cb_gathering_b.pk_fundset");
		strbuf.append(" left outer join bd_balatype gatheringpaymode on gatheringpaymode.pk_balatype=ps_cb_gathering_b.pk_paymode");
		strbuf.append(" left outer join bd_bankdoc bankout on bankout.pk_bankdoc=ps_cb_gathering_b.pk_bank_out");
		strbuf.append(" left outer join bd_bankdoc bankin on bankin.pk_bankdoc=ps_cb_gathering_b.pk_bank_in");		
		strbuf.append(" left outer join bd_bankaccbas on bd_bankaccbas.pk_bankaccbas=ps_cb_gathering_b.pk_accid");	
		strbuf.append(" where isnull(ps_cb_gathering.dr,0)=0");			
		strbuf.append(" and isnull(ps_cb_gathering_b.dr,0)=0");		
		strbuf.append(" and ps_cb_gathering_b.pk_fundset is not null");	
		strbuf.append(" and crm_bd_house.pk_situation is not null");	
		strbuf.append(" and EXISTS (select pk_lastbill from ps_so_sign where ps_so_sign.vlastbill='946D' and ps_so_sign.pk_lastbill=ps_so_subsc.pk_subsc )");// and)
		strbuf.append(" and isnull(ps_cb_gathering.dr,0)=0 ");
		strbuf.append(" and isnull(crm_bd_house.dr,0)=0");
		strbuf.append(" and isnull(ps_so_subsc.dr,0)=0");
		strbuf.append(" and isnull(ps_cmg_customer_sellor.dr,0)=0 and isnull(ps_so_subsc.dr,0)=0  and ps_so_subsc.vbillstatus='");
		strbuf.append(IBillStatus.CHECKPASS);
		strbuf.append("' " + whereSql.replaceAll(COND_BISDMLTN, COND_BISDMLTN_SIGN).replaceAll(COND_PAYMODE, COND_PAYMODE_SIGN));
		strbuf.append(" order by crm_bd_house.pk_project,pk_house,pk_customer ");
		return strbuf.toString();
	}

	/**
	 * 取认购未签约的数据。
	 *
	 * @时间：2008-4-14上午10:55:37
	 * @return
	 */
	private String subscSQL() {
		String whereSql = sql;
		StringBuffer strbuf = new StringBuffer();
		strbuf.append("select  ps_cb_gathering_b.pk_gathering,ps_cb_gathering_b.pk_gathering_b, fdc_bd_project.vname as project,fdc_bd_operstate.vname as situation,ps_cb_gathering.vcustomers as pk_customer,crm_bd_house.pk_subsc, ")
				.append(" ps_cb_gathering.pk_house,ps_bd_fundset.vfsname as  fundset_name,isnull(ps_cb_gathering_b.nbrthisaccountreceiv,0) as mny,gatheringpaymode.balanname as paymode_name , ")
				.append(" ps_cb_gathering_b.nbthisaccountreceiv as shouldmny, ")
				.append("ps_cb_gathering.reserve1,")//add by zhaohf time 2012-03-22  增加交款人
				.append(" ps_so_subsc.nsalearea as nsellarea,ps_so_subsc.ntotalafterdis as nsellmny,crm_bd_house.vhname as house_name, ")
				.append(" crm_bd_house.nsellarea as nfinalarea,isnull(crm_bd_house.nbalancemny,ps_so_subsc.ntotalafterdis) as nfinalmny, ")
				.append(" ps_cb_gathering.vbillno,ps_cb_gathering.fgatherstatus,ps_cb_gathering_b.vchargeno,ps_cb_gathering_b.dchargedate, ")
				.append(" ps_cb_gathering_b.fcardtype, case ps_cb_gathering_b.fcardtype when 0 then '" + NCLangRes4VoTransl.getNCLangRes().getStrByID("HYPS","UPPhyps-001234")//@res "收据"
 + "' when 1 then '" + NCLangRes4VoTransl.getNCLangRes().getStrByID("HYPS","UPPhyps-002514")//@res "发票"
 + "' else '' end as fcardtype," )
				.append(" ps_cb_gathering_b.vcardno,(select bankdocname from bd_bankdoc where pk_bankdoc = ps_cb_gathering_b.pk_bank_in) as pk_bank_in,bd_bankaccbas.accountname as pk_accid,ps_cb_gathering_b.vmark, ")
				.append(" case ps_cb_gathering.fgatherstatus")
				.append(" when 0 then '" + NCLangRes4VoTransl.getNCLangRes().getStrByID("HYPS","UPPHYPS-003311")//@res "未设置"
 + "' when 1 then '" + NCLangRes4VoTransl.getNCLangRes().getStrByID("HYPS","UPPhyps-002550")//@res "准备过账"
 + "' when 2 then '" + NCLangRes4VoTransl.getNCLangRes().getStrByID("HYPS","UPPhyps-002551")//@res "已过账"
 + "' when 3 then '" + NCLangRes4VoTransl.getNCLangRes().getStrByID("HYPS","UPPhyps-002552")//@res "不过账"
 + "' else '" + NCLangRes4VoTransl.getNCLangRes().getStrByID("HYPS","UPPHYPS-003311")//@res "未设置"
 + "' end as fgatherstatus, ")
//						+ " ps_cb_gathering.fgatherstatus  as fgatherstatus, "
                        //zhaohq 20090510修改 按照最初的实现方式，认购未签约的数据无法展现付款方式
						//+ " ps_so_subsc.pk_paymode as subscpay,"
				.append("  signpaymode.vpmname as pk_putmode, ")
				.append("  bd_psndoc.psnname as cussellor,ps_cmg_customer_sellor.bcsellornow ," )
				.append(" ps_so_subsc.bisdmltn as bisdmltn," )
				.append(" case  when ps_so_subsc.bisdmltn is null then '" + NCLangRes4VoTransl.getNCLangRes().getStrByID("HYPS","UPPhyps-002746")//@res "否"
 + "' when ps_so_subsc.bisdmltn='N' then '" + NCLangRes4VoTransl.getNCLangRes().getStrByID("HYPS","UPPhyps-002746")//@res "否"
 + "' when ps_so_subsc.bisdmltn='Y' then '" + NCLangRes4VoTransl.getNCLangRes().getStrByID("HYPS","UPPhyps-002745")//@res "是"
 + "'end as bisdmltn,")
				.append(" bd_deptdoc.deptname as pk_deptdoc,( select psnname from bd_psndoc where pk_psndoc = ps_cb_gathering.pk_psndoc )  as pk_psndoc_gather,ps_bd_fundtype.vftname as pk_fundtype," )
				.append(" (select vfsname from ps_bd_fundset where pk_fundset=ps_cb_gathering_b.voldfund)as voldfund , (select bankdocname from bd_bankdoc where pk_bankdoc = ps_cb_gathering_b.pk_bank_out) as pk_bank_out," )
				.append(" case ps_cb_gathering.fchgbilltype when 0 then '" + NCLangRes4VoTransl.getNCLangRes().getStrByID("HYPS","UPPhyps-001234")//@res "收据"
 + "'when 1 then '" + NCLangRes4VoTransl.getNCLangRes().getStrByID("HYPS","UPPhyps-002514")//@res "发票"
 +"' end as fchgbilltype, ")
				.append( " ps_cb_gathering.pk_chgbilllist as pk_chgbilllist ")
				.append("  from ps_cb_gathering ")
				.append(" inner join crm_bd_house on ps_cb_gathering.pk_house=crm_bd_house.pk_house  ")
				.append(" inner join ps_cb_gathering_b on ps_cb_gathering.pk_gathering=ps_cb_gathering_b.pk_gathering ")
				.append(" inner join ps_cb_gathering_c on ps_cb_gathering.pk_gathering=ps_cb_gathering_c.pk_gathering ")
				.append(" inner join ps_cmg_customer_sellor on ps_cmg_customer_sellor.pk_customer=ps_cb_gathering_c.pk_customer ")
				.append(" inner join bd_psndoc on ps_cmg_customer_sellor.pk_psndoc=bd_psndoc.pk_psndoc ")
				.append(" inner join fdc_bd_project on fdc_bd_project.pk_project=crm_bd_house.pk_project ")
				.append(" inner join fdc_bd_operstate on fdc_bd_operstate.pk_operstate=crm_bd_house.pk_situation ")
				.append(" left outer join ps_so_subsc on crm_bd_house.pk_subsc=ps_so_subsc.pk_subsc ")
				.append(" left outer join ps_bd_paymode signpaymode on signpaymode.pk_paymode=ps_so_subsc.pk_paymode  ")
				.append(" left outer join bd_deptdoc on bd_deptdoc.pk_deptdoc=ps_cb_gathering.pk_deptdoc ")
//				.append(" left outer join sm_user on sm_user.cuserid=ps_cb_gathering.pk_psndoc ")
				.append(" left outer join ps_bd_fundtype on ps_bd_fundtype.pk_fundtype=ps_cb_gathering_b.pk_fundtype ")
				.append(" left outer join ps_bd_fundset on ps_bd_fundset.pk_fundset=ps_cb_gathering_b.pk_fundset ")
				.append(" left outer join bd_balatype gatheringpaymode on gatheringpaymode.pk_balatype=ps_cb_gathering_b.pk_paymode ")
//				.append(" left outer join bd_bankdoc bankout on bankout.pk_bankdoc=ps_cb_gathering_b.pk_bank_out ")
//				.append(" left outer join bd_bankdoc bankin on bankin.pk_bankdoc=ps_cb_gathering_b.pk_bank_in ")
				.append(" left outer join bd_bankaccbas on bd_bankaccbas.pk_bankaccbas=ps_cb_gathering_b.pk_accid ")
//				.append(" left outer join ps_bd_fundset oldset on oldset.pk_fundset=ps_cb_gathering_b.voldfund ")
						// + " inner join ps_so_sign on
						// ps_so_subsc.pk_subsc=ps_so_sign.pk_lastbill and
						// ps_so_sign.vlastbill='946D' "
				.append(" where isnull(ps_cb_gathering.dr,0)=0 and isnull(ps_cb_gathering_b.dr,0)=0 and ps_cb_gathering_b.pk_fundset is not null ")

//						+ " and ps_cb_gathering_b.pk_fundset in (select ps_bd_fundset.pk_fundset from ps_bd_fundset where pk_fundtype  in (select pk_fundtype from ps_bd_fundtype where "
//						+ " vftcode in('01','02','05')) ) "

				.append(" and crm_bd_house.pk_situation is not null ")
						// +
						// " and (ps_so_subsc.dspecialdate is null or ps_so_subsc.dspecialdate>"
						// + timeSql
						// + " ) "
						// + "and ps_so_subsc.pk_subsc not in (select
						// pk_lastbill from ps_so_sign where vlastbill='946D'
						// and isnull(dr,0)=0 and (reserve1 is null or
						// reserve1='') ) "
						// + "and (crm_bd_house.pk_sign is null or
						// crm_bd_house.pk_sign='') and
						// ps_cb_gathering.vbillstatus='"
				.append(" and not EXISTS (select pk_lastbill from ps_so_sign where ps_so_sign.vlastbill='946D' and ps_so_sign.pk_lastbill=ps_so_subsc.pk_subsc )")// and
						// ps_so_sign.dsigndate<="
						// + timeSql
						// + ")  "
				.append(" and isnull(ps_cb_gathering.dr,0)=0 ")
						// + " and ps_cb_gathering.vbillstatus='"
						// + IBillStatus.CHECKPASS
						// + "' "
						// + " and ps_cb_gathering.biscounteract<>'Y' "
				.append(" and isnull(crm_bd_house.dr,0)=0 and isnull(ps_so_subsc.dr,0)=0 " )
//						+ " and (ps_so_subsc.reserve1 is null or ps_so_subsc.reserve1='') "
						// ps_so_subsc.vbillstatus='"
				.append(" and isnull(ps_cmg_customer_sellor.dr,0)=0 and isnull(ps_so_subsc.dr,0)=0  and ps_so_subsc.vbillstatus='")
				.append(IBillStatus.CHECKPASS)
				.append("' " + whereSql.replaceAll(COND_BISDMLTN, COND_BISDMLTN_SUBSC).replaceAll(COND_PAYMODE, COND_PAYMODE_SUBSC));
//		if (customer != null) {
//			strbuf
//					.append("and ps_cb_gathering.pk_gathering in (select ps_cb_gathering_c.pk_gathering from ps_cb_gathering_c  where   ps_cb_gathering_c.pk_customer in ("
//							+ customer + ")) ");
//		}
		strbuf.append(" order by crm_bd_house.pk_project,pk_house,pk_customer ");
		return strbuf.toString();
	}
	/**
	 * <p>功能描述:</p>取未认购签约的数据
	 * <p>创建人及时间： chenhf 2011-9-15下午08:35:45</p>
	 * @return
	 */
	private String unSubscSQL() {
		String whereSql = sql;
		StringBuffer strbuf = new StringBuffer();
		strbuf.append("select  ps_cb_gathering_b.pk_gathering,ps_cb_gathering_b.pk_gathering_b, fdc_bd_project.vname as project,fdc_bd_operstate.vname as situation,ps_cb_gathering.vcustomers as pk_customer,crm_bd_house.pk_subsc, ")
				.append(" ps_cb_gathering.pk_house,ps_bd_fundset.vfsname as  fundset_name,isnull(ps_cb_gathering_b.nbrthisaccountreceiv,0) as mny,gatheringpaymode.balanname as paymode_name , ")
				.append(" ps_cb_gathering_b.nbthisaccountreceiv as shouldmny, ")
				.append(" crm_bd_house.vhname as house_name, ")
				.append("ps_cb_gathering.reserve1,")//add by zhaohf time 2012-03-22  增加交款人
				.append(" ps_cb_gathering.vbillno,ps_cb_gathering.fgatherstatus,ps_cb_gathering_b.vchargeno,ps_cb_gathering_b.dchargedate, ")
				.append(" ps_cb_gathering_b.fcardtype, case ps_cb_gathering_b.fcardtype when 0 then '" + NCLangRes4VoTransl.getNCLangRes().getStrByID("HYPS","UPPhyps-001234")//@res "收据"
 + "' when 1 then '" + NCLangRes4VoTransl.getNCLangRes().getStrByID("HYPS","UPPhyps-002514")//@res "发票"
 + "' else '' end as fcardtype," )
				.append(" ps_cb_gathering_b.vcardno,(select bankdocname from bd_bankdoc where pk_bankdoc = ps_cb_gathering_b.pk_bank_in) as pk_bank_in,bd_bankaccbas.accountname as pk_accid,ps_cb_gathering_b.vmark, ")
				.append(" case ps_cb_gathering.fgatherstatus")
				.append(" when 0 then '" + NCLangRes4VoTransl.getNCLangRes().getStrByID("HYPS","UPPHYPS-003311")//@res "未设置"
 + "' when 1 then '" + NCLangRes4VoTransl.getNCLangRes().getStrByID("HYPS","UPPhyps-002550")//@res "准备过账"
 + "' when 2 then '" + NCLangRes4VoTransl.getNCLangRes().getStrByID("HYPS","UPPhyps-002551")//@res "已过账"
 + "' when 3 then '" + NCLangRes4VoTransl.getNCLangRes().getStrByID("HYPS","UPPhyps-002552")//@res "不过账"
 + "' else '" + NCLangRes4VoTransl.getNCLangRes().getStrByID("HYPS","UPPHYPS-003311")//@res "未设置"
 + "' end as fgatherstatus, ")
				.append("  paymode.vpmname as pk_putmode, ")
				.append("  bd_psndoc.psnname as cussellor,ps_cmg_customer_sellor.bcsellornow ," )
				.append(" ''  as bisdmltn," )
				.append(" bd_deptdoc.deptname as pk_deptdoc,( select psnname from bd_psndoc where pk_psndoc = ps_cb_gathering.pk_psndoc )  as pk_psndoc_gather,ps_bd_fundtype.vftname as pk_fundtype," )
				.append(" (select vfsname from ps_bd_fundset where pk_fundset=ps_cb_gathering_b.voldfund)as voldfund , (select bankdocname from bd_bankdoc where pk_bankdoc = ps_cb_gathering_b.pk_bank_out) as pk_bank_out," )
				.append(" case ps_cb_gathering.fchgbilltype when 0 then '" + NCLangRes4VoTransl.getNCLangRes().getStrByID("HYPS","UPPhyps-001234")//@res "收据"
 + "'when 1 then '" + NCLangRes4VoTransl.getNCLangRes().getStrByID("HYPS","UPPhyps-002514")//@res "发票"
 +"' end as fchgbilltype, ")
				.append( " ps_cb_gathering.pk_chgbilllist as pk_chgbilllist ")
				.append("  from ps_cb_gathering ")
				.append(" inner join ps_cb_gathering_b on ps_cb_gathering.pk_gathering=ps_cb_gathering_b.pk_gathering ")
				.append(" inner join ps_cb_gathering_c on ps_cb_gathering.pk_gathering=ps_cb_gathering_c.pk_gathering ")
				.append(" inner join ps_cmg_customer_sellor on ps_cmg_customer_sellor.pk_customer=ps_cb_gathering_c.pk_customer ")
				.append(" inner join bd_psndoc on ps_cmg_customer_sellor.pk_psndoc=bd_psndoc.pk_psndoc ")
				.append(" inner join fdc_bd_project on fdc_bd_project.pk_project=ps_cb_gathering.pk_project ")
				.append(" left outer join crm_bd_house on ps_cb_gathering.pk_house=crm_bd_house.pk_house ")
				.append(" left outer join fdc_bd_operstate on fdc_bd_operstate.pk_operstate=crm_bd_house.pk_situation ")
				.append(" left outer join ps_bd_paymode paymode on paymode.pk_paymode=ps_cb_gathering.pk_paymode  ")
				.append(" left outer join bd_deptdoc on bd_deptdoc.pk_deptdoc=ps_cb_gathering.pk_deptdoc ")
//				.append(" left outer join sm_user on sm_user.cuserid=ps_cb_gathering.pk_psndoc ")
				.append(" left outer join ps_bd_fundtype on ps_bd_fundtype.pk_fundtype=ps_cb_gathering_b.pk_fundtype ")
				.append(" left outer join ps_bd_fundset on ps_bd_fundset.pk_fundset=ps_cb_gathering_b.pk_fundset ")
				.append(" left outer join bd_balatype gatheringpaymode on gatheringpaymode.pk_balatype=ps_cb_gathering_b.pk_paymode ")
				.append(" left outer join bd_bankaccbas on bd_bankaccbas.pk_bankaccbas=ps_cb_gathering_b.pk_accid ")
				.append(" where isnull(ps_cb_gathering.dr,0)=0 and isnull(ps_cb_gathering_b.dr,0)=0 and ps_cb_gathering_b.pk_fundset is not null ")
//				.append(" and crm_bd_house.pk_situation is not null ")
				.append(" and ((crm_bd_house.pk_house is not null and (crm_bd_house.pk_sign is null or crm_bd_house.pk_sign='') and (crm_bd_house.pk_subsc is null or crm_bd_house.pk_subsc='') ) or crm_bd_house.pk_house is  null ) ")
				.append(" and isnull(ps_cb_gathering.dr,0)=0 ")
				.append(" and isnull(crm_bd_house.dr,0)=0 " )
				.append(" and isnull(ps_cmg_customer_sellor.dr,0)=0 ")
				.append(whereSql.replaceAll("crm_bd_house.pk_project", "ps_cb_gathering.pk_project"));
//		}
		strbuf.append(" order by pk_customer ");
		return strbuf.toString();
	}

	private Collection<String> splidCusts(CircularlyAccessibleValueObject vo) {
		if (vo == null || CommonUtil.isNullStr(vo.getAttributeValue(CrRevfareVO.PK_CHGBILLLIST)))
			return new ArrayList<String>();
		String sCusts = CommonUtil.initStr(vo.getAttributeValue(CrRevfareVO.PK_CHGBILLLIST));
		String[] aCusts = sCusts.split(",");
		return Arrays.asList(aCusts);
	}
	
	private void setComboValueOfSign(ReportBaseVO[] signVOs){
		for (int i = 0; i < signVOs.length; i++) {
			initComboBoxOfFcardtype(signVOs[i]);
			initComboBoxOfBisdmltn(signVOs[i]);
			initComboBoxOffchgbilltype(signVOs[i]);
			initComboBoxOfFgatherstatus(signVOs[i]);
		}
	}
	
	
	private void setComboValueOfSubscsign(ReportBaseVO[] subscsignVOs){
		for (int i = 0; i < subscsignVOs.length; i++) {
			initComboBoxOfFcardtype(subscsignVOs[i]);
			initComboBoxOfFgatherstatus(subscsignVOs[i]);
			initComboBoxOfBisdmltn(subscsignVOs[i]);
			initComboBoxOffchgbilltype(subscsignVOs[i]);
		}
	}
	
	//票据类型
	private void initComboBoxOfFcardtype(ReportBaseVO reportVo){
		String vsFcardtype=reportVo.getAttributeValue("fcardtype")==null?"":reportVo.getAttributeValue("fcardtype").toString();
		if(vsFcardtype.equals("0")){
			reportVo.setAttributeValue("fcardtype", "收据"); 
		}else if(vsFcardtype.equals("1")){
			reportVo.setAttributeValue("fcardtype", "发票"); 
		}else {
			reportVo.setAttributeValue("fcardtype", ""); 
		}
	}
	
	
	//是否拆迁
	private void initComboBoxOfBisdmltn(ReportBaseVO reportVo){
		String vsBisdmltn=reportVo.getAttributeValue("bisdmltn")==null? "否":reportVo.getAttributeValue("bisdmltn").toString();
		if(vsBisdmltn.equals("N")){
			reportVo.setAttributeValue("bisdmltn", "否"); 
		}else if(vsBisdmltn.equals("Y")){
			reportVo.setAttributeValue("bisdmltn", "是"); 
		}else {
			reportVo.setAttributeValue("fcardtype", vsBisdmltn); 
		}

	}
	
	//换票类弄
	private void initComboBoxOffchgbilltype(ReportBaseVO reportVo){
		String vsFchgbilltype=reportVo.getAttributeValue("fchgbilltype")==null? "":reportVo.getAttributeValue("fchgbilltype").toString();
		if(vsFchgbilltype.equals("0")){
			reportVo.setAttributeValue("fchgbilltype", "收据"); 
		}else if(vsFchgbilltype.equals("1")){
			reportVo.setAttributeValue("fchgbilltype", "发票"); 
		}else {
			reportVo.setAttributeValue("fchgbilltype", vsFchgbilltype); 
		}
	}
	
	//过账状态
	private void initComboBoxOfFgatherstatus( ReportBaseVO reportVo){
		String vsFgatherstatus=reportVo.getAttributeValue("fgatherstatus")==null? "未设置":reportVo.getAttributeValue("fgatherstatus").toString();
		if(vsFgatherstatus.equals("0")){
			reportVo.setAttributeValue("fgatherstatus", "未设置'"); 
		}else if(vsFgatherstatus.equals("1")){
			reportVo.setAttributeValue("fgatherstatus", "准备过账"); 
		}else if(vsFgatherstatus.equals("2")){  
			reportVo.setAttributeValue("fgatherstatus", "已过账"); 
		}else if(vsFgatherstatus.equals("3")){ 
			reportVo.setAttributeValue("fgatherstatus", "不过账"); 
		}else {
			reportVo.setAttributeValue("fgatherstatus", vsFgatherstatus); 
		}
	}

}
