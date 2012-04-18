package nc.ui.ps.report.report4554;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.jzfdc.pub.IAnalyzeXML;
import nc.itf.jzfdc.pub.report.IPeneExtendInfo;
import nc.itf.jzfdc.pub.report.IUFTypes;
import nc.itf.ps.pub.IPSAnalyzeXML;
import nc.itf.ps.pub.IPSModuleCode;
import nc.itf.ps.pub.IPSPub;
import nc.itf.uap.rbac.function.IFuncPower;
import nc.ui.fdc.pub.PmUIProxy;
import nc.ui.jzfdc.report.PMLinkQueryData;
import nc.ui.jzfdc.report.ReportBO_Client;
import nc.ui.jzfdc.report.buttonactoin.IReportButton;
import nc.ui.ml.NCLangRes;
import nc.ui.pf.pub.PfUIDataCache;
import nc.ui.ps.pub.customer.PenerateToCustomer;
import nc.ui.ps.report.pub.PSReportBaseUI;
import nc.ui.ps.report.pub.PSReportPubOpreate;
import nc.ui.pub.ClientEnvironment;
import nc.ui.pub.FuncNodeStarter;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UITable;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.linkoperate.ILinkType;
import nc.ui.sm.power.FuncRegister;
import nc.ui.trade.report.query.QueryDLG;
import nc.ui.uap.sf.SFClientUtil;
import nc.vo.crmbd.pub.tools.CommonUtil;
import nc.vo.jzfdc.pub.NodeXMLData;
import nc.vo.jzfdc.pub.report.ReportBaseVO;
import nc.vo.jzfdc.pub.report.SubtotalVO;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.ps.pub.CmgCustomerVO;
import nc.vo.ps.pub.bd.DbTempTableDMO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.query.ConditionVO;
import nc.vo.sm.funcreg.FuncRegisterVO;

/**
 * @作者 zhfa
 * @创建时间：2007-5-14 上午10:13:37
 * @修改者：ssd
 * @修改时间：2007-5-14 上午10:13:37
 * @说明：H3014554	房产回款汇总表	nc.ui.ps.report.report4554.ReportUI
 */
@SuppressWarnings("deprecation")
public class ReportUI extends PSReportBaseUI {


	private static final long serialVersionUID = 1L;

	private ReportQueryDlg m_qryDlg = null;

	public ReportUI() {
	}

	@Override
	public String _getModelCode() {
		
		return IPSModuleCode.SUMMARY_HOUSE_BACKMNY;
	}

	@Override
	public int[] getReportButtonAry() {
		return new int[] { IReportButton.QueryBtn, IReportButton.PrintBtn };
	}

	/**
	 * @创建者：zhfa
	 * @方法说明：
	 * @创建时间：2007-5-14 上午10:13:38
	 * @修改者：ssd
	 * @修改时间：2007-5-14 上午10:13:38
	 * @override
	 *
	 */
	@Override
	public void onQuery() {
		
		try {
			getQueryDlg().showModal();
			if (getQueryDlg().getResult() == UIDialog.ID_OK) {
				String querySql = getQueryDlg().getWhereSQL();
				try {
					queryByCustomWhereClause(querySql);
				} catch (Exception e) {
					showWarningMessage(e.getMessage());
				}
			}
		} catch (Exception e) {
			showWarningMessage(e.getMessage());
		}
	}
	


	private void total() throws Exception {
		SubtotalVO svo = new SubtotalVO();
		svo.setGroupFldCanNUll(true);// 分组列的数据是否可以为空。
		svo.setAsLeafRs(new boolean[] { false });// 分组列合并后是否作为末级节点记录。
		svo.setValueFlds(new String[] { "shouldmny","reserve6","balancemny", "getmny", "arrearage" });// 求值列:
		svo.setValueFldTypes(new int[] { IUFTypes.UFD, IUFTypes.UFD,IUFTypes.UFD,IUFTypes.UFD, IUFTypes.UFD });// 求值列的类型:
		svo.setTotalDescOnFld("project");// 合计所在列
		setSubtotalVO(svo);
		doSubTotal();
	}

	protected void queryByCustomWhereClause(String querySql) throws Exception {
		// ZYT 自动生成方法存根
		if (querySql == null || querySql.equals("")) {
			querySql = " 1=1";
		}

		ReportBaseVO[] vos = getSQL(querySql);
		ReportBaseVO headVO = new ReportBaseVO();
		Condition(headVO);
		getReportBase().setHeadDataVO(headVO);
		getReportBase().execHeadFormulas(
				new String[] { "pk_project->getColValue(fdc_bd_project,vname,pk_project,pk_project);",
						"pk_house->getColValue(crm_bd_house,vhname,pk_house,pk_house);",
						"pk_building->getColValue(crm_bd_building,vbuildingname,pk_building,pk_building);" });
		if (vos == null || vos.length == 0) {
			setBodyVO(null);
			return;
		}

//		setBodyVO(rebuildVO(vos));
		setBodyVO(vos);
		getReportBase().getBillModel().execFormulas(
				new String[] {
						"arrearage->balancemny-getmny;"
						});

		total();

	}

	private void fillSubOrSignPK(ReportBaseVO[] baseVOs){
		List<String> housePKList = new ArrayList<String>();
		for(ReportBaseVO vo:baseVOs){
			housePKList.add(vo.getStringValue("pk_house"));
		}
		//,isnull(ps_so_sign.ntotalmnysign,ps_so_subsc.ntotalafterdis) as shouldmny
		StringBuilder sb = new StringBuilder();
		sb.append("select distinct house.pk_house,sign.pk_sign,sub.pk_subsc,sign.ntotalmnysign,sub.ntotalafterdis  ");
		sb.append("from crm_bd_house house ");
		sb.append("left join ps_so_subsc sub on sub.pk_house=house.pk_house ");
		sb.append("left join ps_so_sign sign on sign.pk_house=house.pk_house ");
		sb.append(" where ((isnull(sub.dr,0)=0 and isnull(sub.vbillstatus,1)=1 and sub.reserve1 is null ) or (  ");
		sb.append(" isnull(sign.dr,0)=0 and isnull(sign.vbillstatus,1)=1 and sign.reserve1 is null)) ");


		ReportBaseVO[] housesubsignvos = null;
				try {
					IPSPub service = NCLocator.getInstance().lookup(
							IPSPub.class);
					housesubsignvos = (ReportBaseVO[]) service
							.queryByCondWithTempTableIfNeed(ReportBaseVO.class,
									"house.pk_house", housePKList
											.toArray(new String[0]), sb
											.toString(),null);
				} catch (Exception e) {

				}
//					PmUIProxy.getIReportService().queryVOBySql(sb.toString());
				if(housesubsignvos!=null && housesubsignvos.length>0){
					Map<String, ReportBaseVO> map = new HashMap<String, ReportBaseVO>();
					for(ReportBaseVO vv:housesubsignvos)
						map.put(vv.getStringValue("pk_house"), vv);
					for(ReportBaseVO oo:baseVOs){
						String pk_house = oo.getStringValue("pk_house");
						if(map.containsKey(pk_house)){
							ReportBaseVO ss = map.get(pk_house);
							String pk_sign = ss.getStringValue("pk_sign");
							String pk_subsc = ss.getStringValue("pk_subsc");
							if(pk_sign!=null ){
								oo.setAttributeValue("pk_sign", pk_sign);
								oo.setAttributeValue("pk_subsc", null);
								oo.setAttributeValue("shouldmny", ss.getAttributeValue("ntotalmnysign"));

							}else{
								oo.setAttributeValue("pk_subsc", pk_subsc);
								oo.setAttributeValue("pk_sign", null);
								oo.setAttributeValue("shouldmny", ss.getAttributeValue("ntotalafterdis"));
							}
							UFDouble reserve6 = oo.getDoubleValue("reserve6");
							if(reserve6==null || reserve6.doubleValue()==0){
								String pk_area = oo.getStringValue("pk_arearepair");
								if(pk_area == null)
									oo.setAttributeValue("balancemny", oo.getAttributeValue("shouldmny"));
								else{
									oo.setAttributeValue("reserve6", oo.getDoubleValue("nmarginrepair").add(oo.getDoubleValue("shouldmny")));
									oo.setAttributeValue("balancemny", oo.getDoubleValue("reserve6"));
								}
							}
							else
								oo.setAttributeValue("balancemny", oo.getAttributeValue("reserve6"));

						}
					}
				}

			}

	/**、
	 * 获取房产的客户
	 * @param baseVOs
	 * @return
	 */
	public ReportBaseVO[] rebuildVO(ReportBaseVO[] baseVOs) {
		if (baseVOs != null && baseVOs.length > 0) {

			fillSubOrSignPK(baseVOs);

			List<String> subList = new ArrayList<String>();
			List<String> signList = new ArrayList<String>();
			for (ReportBaseVO vo : baseVOs) {
				if (vo.getStringValue("pk_sign") != null
						&& vo.getStringValue("pk_sign").length() > 0){
					UFDouble reserve6 = vo.getDoubleValue("reserve6");
					if(reserve6==null || reserve6.doubleValue()==0)
						vo.setAttributeValue("balancemny", vo.getAttributeValue("shouldmny"));
					else
						vo.setAttributeValue("balancemny", vo.getAttributeValue("reserve6"));
					signList.add(vo.getStringValue("pk_sign"));
				}
				else {
					vo.setAttributeValue("balancemny",vo.getAttributeValue("shouldmny"));
					if (vo.getStringValue("pk_subsc") != null
							&& vo.getStringValue("pk_subsc").length() > 0)
						subList.add(vo.getStringValue("pk_subsc"));
				}
			}
			if (signList.size() > 0) {
				try {
					String[] signPKarr = signList.toArray(new String[0]);
					StringBuilder sb = new StringBuilder();
					sb
							.append(" select sign.pk_sign,cus.pk_customer,cus.vcname as name");
					sb
							.append(" from ps_so_sign sign "
									+ "left join ps_so_sign_customer signcus on sign.pk_sign = signcus.pk_sign "
									+ "inner join ps_cmg_customer cus on signcus.pk_customer = cus.pk_customer ");
					sb
							.append(" where isnull(sign.dr,0)=0 and isnull(signcus.dr,0)=0 and isnull(cus.dr,0)=0 ");

					DbTempTableDMO dmoTempTbl = null;
					String signPKSql = null;
					if (signPKarr != null && signPKarr.length > 500) {
						if (dmoTempTbl == null)
							dmoTempTbl = new DbTempTableDMO();
						signPKSql = dmoTempTbl.insertTempTable(signPKarr,
								"temtable", "tempksign");
					} else if (signPKarr == null || signPKarr.length == 0)
						signPKSql = null;
					else
						signPKSql = " (" + CommonUtil.arrayToString(signPKarr)
								+ ")";

					if (signPKSql != null) {
						ReportBaseVO[] signNames = PmUIProxy
								.getIReportService()
								.queryVOBySql(
										sb.append(
												" and sign.pk_sign in "
														+ signPKSql).toString());
						Map<String, List<String>> signNameMap = new HashMap<String, List<String>>();
						if (signNames != null && signNames.length > 0) {
							for (ReportBaseVO namevo : signNames) {
								String key = namevo.getStringValue("pk_sign");
								if (signNameMap.keySet().contains(key))
									signNameMap.get(key).add(
											namevo.getStringValue("name"));
								else {
									List<String> list = new ArrayList<String>();
									list.add(namevo.getStringValue("name"));
									signNameMap.put(key, list);
								}

							}
						}
						for (ReportBaseVO vo : baseVOs) {
							String key = vo.getStringValue("pk_sign");
							if (signNameMap.keySet().contains(key)) {
								List<String> l = signNameMap.get(key);
								vo.setAttributeValue("customer",
										getCombinName(l));
							}
						}
					}

				} catch (Exception e) {
				}
			}
			if (subList.size() > 0) {
				try {
					String[] subArr = subList.toArray(new String[0]);
					StringBuilder sb = new StringBuilder();
					sb
							.append(" select sub.pk_subsc,cus.pk_customer,cus.vcname as name");
					sb
							.append(" from ps_so_subsc sub "
									+ "left join ps_so_subsc_customer subcus on sub.pk_subsc = subcus.pk_subsc "
									+ "inner join ps_cmg_customer cus on subcus.pk_customer = cus.pk_customer ");
					sb
							.append(" where isnull(sub.dr,0)=0 and isnull(subcus.dr,0)=0 and isnull(cus.dr,0)=0 ");

					DbTempTableDMO dmoTempTbl = null;
					String signPKSql = null;
					if (subArr != null && subArr.length > 500) {
						if (dmoTempTbl == null)
							dmoTempTbl = new DbTempTableDMO();
						signPKSql = dmoTempTbl.insertTempTable(subArr,
								"temtable", "tempksign");
					} else if (subArr == null || subArr.length == 0)
						signPKSql = null;
					else
						signPKSql = " (" + CommonUtil.arrayToString(subArr)
								+ ")";

					if (signPKSql != null) {
						ReportBaseVO[] signNames = PmUIProxy
								.getIReportService()
								.queryVOBySql(
										sb.append(
												" and sub.pk_subsc in "
														+ signPKSql).toString());
						Map<String, List<String>> signNameMap = new HashMap<String, List<String>>();
						if (signNames != null && signNames.length > 0) {
							for (ReportBaseVO namevo : signNames) {
								String key = namevo.getStringValue("pk_subsc");
								if (signNameMap.keySet().contains(key))
									signNameMap.get(key).add(
											namevo.getStringValue("name"));
								else {
									List<String> list = new ArrayList<String>();
									list.add(namevo.getStringValue("name"));
									signNameMap.put(key, list);
								}

							}
						}
						for (ReportBaseVO vo : baseVOs) {
							String sign = vo.getStringValue("pk_sign");
							if(sign!=null && sign.length()>0)
								continue;
							String key = vo.getStringValue("pk_subsc");
							if (signNameMap.keySet().contains(key)) {
								List<String> l = signNameMap.get(key);
								vo.setAttributeValue("customer",
										getCombinName(l));
							}
						}
					}

				} catch (Exception e) {
				}
			}
		}
		return baseVOs;
	}

	private String getCombinName(List<String> list){
		if(list!=null){
			if(list.size()==1)
				return list.get(0);
			else{
				String rt = "";
				for(String name:list)
					rt+=","+name;
				return rt;
			}
		}
		return null;
	}

	private void Condition(ReportBaseVO headVO) throws Exception {
		ConditionVO[] QueueVO = getQueryDlg().getConditionVO();
		if (QueueVO != null) {
			for (int i = 0; i < QueueVO.length; i++) {
				if (QueueVO[i].getFieldName().equals(NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0004165")//@res "项目"
)) {
					headVO.setAttributeValue("pk_project", QueueVO[i].getValue());
				}
				if (QueueVO[i].getFieldName().equals(NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0003246")//@res "结算开始日期"
)) {
					headVO.setAttributeValue("begintime", QueueVO[i].getValue());
				}
				if (QueueVO[i].getFieldName().equals(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-001828")//@res "结算结束日期"
)) {
					headVO.setAttributeValue("endtime", QueueVO[i].getValue());
				}
				if (QueueVO[i].getFieldName().equals(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011015-000149")//@res "楼栋"
)) {
					headVO.setAttributeValue("pk_building", QueueVO[i].getValue());
					// flag=true;
				}
				if (QueueVO[i].getFieldName().equals(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000545")//@res "房产"
)) {
					headVO.setAttributeValue("pk_house", QueueVO[i].getValue());
				}
				if (QueueVO[i].getFieldName().equals(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3010545-000001")//@res "单元"
)) {
					headVO.setAttributeValue("vhcell", QueueVO[i].getValue());
				}
			}
		}
	}

	private ReportBaseVO[] getSQL(String querySql) throws Exception {
		String sql = " and 1=1 ";
		ConditionVO[] Condition = getQueryDlg().getConditionVO();
		String gathingstate = null; // 起始日期
		String gathingend = null; // 截止日期
		@SuppressWarnings("unused")
		String pk_project = null;
		if (Condition != null && Condition.length > 0) {
			for (int i = 0; i < Condition.length; i++) {
				if (Condition[i].getFieldCode().equals("ps_cb_gathering.pk_project")) {
					sql = sql + " and crm_bd_house.pk_project in("
							+ new PSReportPubOpreate().getChildProjPKByWherePart(Condition[i].getValue()) + ")";
					pk_project = " and ps_cb_gathering.pk_project in("
							+ new PSReportPubOpreate().getChildProjPKByWherePart(Condition[i].getValue()) + ") ";
				}
				if (Condition[i].getFieldCode().equals("ps_cb_gathering_b.dchargedate1")) {
					gathingstate = "and  ps_cb_gathering_b.dchargedate>='" + Condition[i].getValue() + "'";
				}
				if (Condition[i].getFieldCode().equals("ps_cb_gathering_b.dchargedate2")) {
					gathingend = "and ps_cb_gathering_b.dchargedate<='" + Condition[i].getValue() + "'";
				}
				if (Condition[i].getFieldCode().equals("crm_bd_house.pk_building")) {
					sql = sql + " and crm_bd_house.pk_building='" + Condition[i].getValue() + "'";
				}
				if (Condition[i].getFieldCode().equals("crm_bd_house.pk_situation")) {
					sql = sql + " and crm_bd_house.pk_situation='" + Condition[i].getValue() + "'";
				}
				if (Condition[i].getFieldCode().equals("ps_cb_gathering.pk_house")) {
					sql = sql + " and crm_bd_house.pk_house='" + Condition[i].getValue() + "'";
				}
				if (Condition[i].getFieldCode().equals("crm_bd_house.vhcell")) {
					sql = sql + " and crm_bd_house.vhcell='" + Condition[i].getValue() + "'";
				}
				// add by zhaohf time 2012-03-19 start
				if (Condition[i].getFieldCode().equals("ps_cmg_customer_sellor.pk_psndoc")){
					sql +=  " and ps_cmg_customer_sellor.pk_psndoc='" + Condition[i].getValue() + "'";
				}
				// add by zhaohf time 2012-03-19 end
				
			}
			//修改人  赵华峰  修改时间： 2012.3.9  修改原因：更名 换房 退房 都改为原信息不显示 start   
			 
				sql += " and ps_cb_gathering_c.pk_customer not in (select ps_so_changename_odd.pk_customer  from ps_so_changename inner join ps_so_changename_odd  on ps_so_changename.pk_changename =  ps_so_changename_odd.pk_changename  where ps_so_changename.pk_house = crm_bd_house.pk_house and ps_so_changename.vbillstatus = 1) ";
				sql += " and ps_cb_gathering_c.pk_customer not in ( select ps_so_hchange_cust.pk_customer  from ps_so_housechange inner join ps_so_hchange_cust on ps_so_housechange.pk_housechange =  ps_so_hchange_cust.pk_housechange where ps_so_housechange.pk_house_old = crm_bd_house.pk_house  and ps_so_housechange.vbillstatus = 1 ) ";
				sql += " and ps_cb_gathering_c.pk_customer not in ( select ps_so_sign_customer.pk_customer from ps_so_sign inner join  ps_so_sign_customer on ps_so_sign.pk_sign = ps_so_sign_customer.pk_sign where ps_so_sign.pk_house = ps_cb_gathering.pk_house and ps_so_sign.reserve1 =1 ) ";
				sql += " and ps_cb_gathering_c.pk_customer not in (select ps_so_subsc_customer.pk_customer from ps_so_subsc inner join  ps_so_subsc_customer on ps_so_subsc.pk_subsc = ps_so_subsc_customer.pk_subsc where ps_so_subsc.pk_house = ps_cb_gathering.pk_house and ps_so_subsc.reserve1 =1 )";
			// end	退房SQL 与认购签约进行关联，查询出是否为退房，退房弃审后查不出来，可查看结算日期
		}
		ReportBaseVO[] rstVOs = gathingSQL(gathingstate, gathingend, sql);
		return rstVOs;
	}

	/**
	 * 取认购并签约的签约这前的数据
	 * @时间：2008-3-31下午01:40:17
	 * @param gathingstate
	 * @param gathingend
	 * @param endtime
	 * @param pk_project
	 * @return
	 * @throws BusinessException
	 */
	private ReportBaseVO[] gathingSQL(String gathingstate, String gathingend, String pk_project) throws BusinessException {

		String sql = pk_project;
		if (gathingstate != null) {
			sql = sql + gathingstate;
		}
		if (gathingend != null) {
			sql = sql + gathingend;
		}

		StringBuilder exeSql = new StringBuilder();
		exeSql.append("  select distinct ps_cb_gathering.pk_house ,crm_bd_house.pk_building, crm_bd_house.pk_situation ," +
				"fdc_bd_project.pk_project as pk_project,fdc_bd_project.vname as project,fdc_bd_operstate.vname as situation," +
				"crm_bd_building.vbuildingname as building,"+
				" ps_cb_gathering.vcustomers as customer,"+
				" ps_so_arearepair.pk_arearepair,ps_so_arearepair.ncontrepair,"+
//				"ps_so_sign.pk_sign,ps_so_subsc.pk_subsc, " +
				"crm_bd_house.vhnum as houseno,crm_bd_house.vhname as housename, ")
		.append(
				"crm_bd_house.reserve6, " +
				" sum(isnull(ps_cb_gathering_b.nbrthisaccountreceiv,0)) as getmny," +
				" bd_psndoc.psnname  as cussellor  " // add by zhaohf time 2012-03-19   置业顾问
			   // + "ps_cmg_customer.vpreferredtel as vtel, " //add by zhaohf time 2012-03-19   客户电话
			   // + "ps_cmg_customer.vcaddress as vaddress, " //add by zhaohf time 2012-03-19   客户地址
			   // + "ps_cmg_customer.pk_customer " //add by zhaohf time 2012-03-19   客户主键
			   // + "ps_bd_fundset.vfsname, " //add by zhaohf time 2012-03-19  款项
			   // + "ps_bd_fundtype.vftcode " //add by zhaohf time 2012-03-19  款项类型
				+ " from crm_bd_house "
				+ " inner join ps_cb_gathering on  ps_cb_gathering.pk_house=crm_bd_house.pk_house "
				+ " inner join fdc_bd_operstate on fdc_bd_operstate.pk_operstate = crm_bd_house.pk_situation "
				+ " inner join crm_bd_building on crm_bd_building.pk_building = crm_bd_house.pk_building "
				+ " inner join fdc_bd_project on fdc_bd_project.pk_project = crm_bd_house.pk_project "
				+ " inner join ps_cb_gathering_b on ps_cb_gathering.pk_gathering=ps_cb_gathering_b.pk_gathering "


				+ " left join ps_so_arearepair on crm_bd_house.pk_house=ps_so_arearepair.pk_house and ps_so_arearepair.vbillstatus=1 "
//				+" left join ps_so_subsc on ps_cb_gathering.pk_house=ps_so_subsc.pk_house "
				//add by zhaohf time 2012-03-19  置业顾问 start
				+ "  left join ps_cb_gathering_c on ps_cb_gathering.pk_gathering = ps_cb_gathering_c.pk_gathering left join ps_cmg_customer_sellor on ps_cmg_customer_sellor.pk_customer = ps_cb_gathering_c.pk_customer" +
				 " left join bd_psndoc on ps_cmg_customer_sellor.pk_psndoc = bd_psndoc.pk_psndoc "
				+ " left join ps_bd_fundset   on ps_bd_fundset.pk_fundset =  ps_cb_gathering_b.pk_fundset  " //款项
				+ " left join ps_bd_fundtype  on ps_bd_fundset.pk_fundtype = ps_bd_fundtype.pk_fundtype " //款项类型
				//add by zhaohf time 2012-03-19  置业顾问 end

				+ " where ps_cb_gathering_b.pk_fundset is not null and isnull(ps_cb_gathering_b.dr,0)=0 and isnull(ps_so_arearepair.dr,0)=0 "
				//款项 只统计房款 和 车位款  订金不统计
				+ " and ps_bd_fundtype.vftcode in('01', '02') "//modefied by zhaohf  

				//房产回款汇总的统计不需要约束认购和签约的状态 @modified by zhangws
//				+ " and (ps_so_sign.pk_sign is null or (ps_so_sign.vbillstatus='"+IBillStatus.CHECKPASS+"' " +"and isnull(ps_so_sign.dr,0)=0  " +
//						" )) "

				//房产回款汇总的统计不需要约束认购和签约的状态
				+ " and isnull(ps_cb_gathering.dr,0)=0 and  ps_cb_gathering.pk_house is not null " + sql
				+ " group by ps_cb_gathering.pk_house,ps_cb_gathering.pk_project,crm_bd_house.pk_building, " +
						"fdc_bd_project.pk_project,fdc_bd_project.vname,crm_bd_building.vbuildingname,fdc_bd_operstate.vname,"+
						"crm_bd_house.pk_situation,ps_so_arearepair.pk_arearepair,ps_so_arearepair.ncontrepair," +
//						"ps_so_sign.pk_sign,ps_so_subsc.pk_subsc," +
//						"isnull(ps_so_sign.ntotalmnysign,ps_so_subsc.ntotalafterdis)" +
						"crm_bd_house.reserve6,ps_cb_gathering.vcustomers," +
						"crm_bd_house.vhnum,crm_bd_house.vhname" +
						//" , bd_psndoc.psnname,ps_cmg_customer.vpreferredtel, ps_cmg_customer.vcaddress,ps_cmg_customer.pk_customer ");// add by zhaohf
						" , bd_psndoc.psnname ");// add by zhaohf

		exeSql.append(" order by fdc_bd_project.vname,fdc_bd_operstate.vname,crm_bd_building.vbuildingname ");
		ReportBaseVO[] bVO = PmUIProxy.getIReportService().queryVOBySql(exeSql.toString());

		if(bVO!=null && bVO.length>0)
			fillSubOrSignPK(bVO);

		return bVO;

	}

	@Override
	public QueryDLG getQueryDlg() {
		// ZYT 自动生成方法存根
		if (m_qryDlg == null) {
			m_qryDlg = new ReportQueryDlg(this);
			m_qryDlg.setTempletID(_getCorpID(), _getModelCode(), _getUserID(), null);

		}
		return m_qryDlg;
	}

	/**
	 * @创建者：zhfa
	 * @方法说明：
	 * @创建时间：2007-5-14 上午10:13:38
	 * @修改者：ssd
	 * @修改时间：2007-5-14 上午10:13:38
	 * @override
	 *
	 */
	@Override
	public void setUIAfterLoadTemplate() {
	}

	/**
	 *
	 * @创建者：成军
	 * @方法说明：
	 * @创建时间：2007-12-14 下午03:30:32
	 * @修改者：Administrator
	 * @修改时间：2007-12-14 下午03:30:32
	 * @return
	 * @override
	 *
	 */
	public String getPene2NodeInfo() {
		return "H3014554";
	}

	@Override
	public boolean isPage() {
		return false;
	}
	

	public void onPenerate() {
		//根据表头的结算日期穿透符合时间条件的收款单
		ReportBaseVO headVO = (ReportBaseVO)getReportBase().getHeadDataVO();
		String gathingstate = null; // 结算起始日期
		String gathingend = null; // 结算截止日期
		gathingstate = (String)headVO.getAttributeValue("begintime");
		gathingend = (String)headVO.getAttributeValue("endtime");
		//extendSql为拼凑的符合表头查询条件的sql语句，
		//因为onpenerate（）方法将nodeXMLData.getLinkdatatype()赋值给linkdata的userObject属性，userObject为穿透的查询条件
		//所以将extendSql也加到userObject中
		StringBuffer extendSql = new StringBuffer().append("select ps_cb_gathering_b.PK_GATHERING from ps_cb_gathering_b ps_cb_gathering_b  where ps_cb_gathering_b.PK_GATHERING=ps_cb_gathering.PK_GATHERING ");
		if(gathingstate!=null && gathingend!=null){
			extendSql.append(" and ").append(" ps_cb_gathering_b.dchargedate >= '").append(gathingstate).append("' ");
			extendSql.append(" and ").append(" ps_cb_gathering_b.dchargedate <= '").append(gathingend).append("' ");
		}
		// 获取所取的行VO
		ReportBaseVO rowVo = getSelectedVO();
		if (rowVo == null) {
			showHintMessage(NCLangRes4VoTransl.getNCLangRes().getStrByID("H0","UPPH0-002277")//@res "本行本列不支持穿透!"
);
			return;
		}
		String itemkey = getItemKey();
		// add by zhaohf 客户信息穿透 start
		if("customer".equals(itemkey)|| "vtel".equals(itemkey) || "vaddress".equals(itemkey)){
			CmgCustomerVO cvo = new CmgCustomerVO(); 
			cvo.setAttributeValue("pk_customer", rowVo.getAttributeValue("pk_customer"));
			cvo.setFccategory(0);
			new PenerateToCustomer().onPenerate(this, getModuleCode(), cvo);
			return ;
		}
		// add by zhaohf 客户信息穿透 end
		Object settleMny = rowVo.getAttributeValue("nmnyb5");
		if (itemkey.equals("nmnyb5")&&settleMny != null && settleMny.toString().trim().length() > 0) {
			specailPenerate();
		} else {

			PMLinkQueryData linkdata = new PMLinkQueryData();
			String destFunCode;
			// 得到当前报表模板的NodeCode
			String reportNode = getPene2NodeInfo();
			String[] extendInfo = getExtendInfoFromPeneInfo(getExtendInfo(),
					itemkey);
			if (reportNode != null && rowVo.getStringValue(itemkey) != null) {
				// 调用远程接口类，通过远程XML解析类解析XML文件，并将数据处理成 NodeXMLData实例返回
				IAnalyzeXML analyzeXMLImpl = NCLocator
						.getInstance().lookup(IPSAnalyzeXML.class);

				NodeXMLData nodeXMLData;
				try {

					// add by zhourj 修改时间：09/01/12 可穿透到不同目标对象
					String penetrateObj = getPenetrateObj();
					if (null == penetrateObj || "".equals(penetrateObj)) {
						nodeXMLData = analyzeXMLImpl.analyzeNodeXML(reportNode,
								itemkey, rowVo, extendInfo);
					} else if ("-1".equals(penetrateObj))// 取消按钮返回
					{
						return;
					} else {
						nodeXMLData = analyzeXMLImpl.analyzeNodeXML(reportNode,
								itemkey, rowVo, extendInfo, penetrateObj);
					}

					if ("billtype".equals(nodeXMLData.getLinktype())) {
						// 如果存放的是单据类型,那么destFunCode也就是待打开节点code通过单据类型去获取
						destFunCode = PfUIDataCache.getBillType(
								nodeXMLData.getCode()).getNodecode();
					} else {
						// 如果存放的是nodecode，那么该nodecode就是待打开节点的code
						destFunCode = nodeXMLData.getCode();
					}
					if ("strwhere".equals(nodeXMLData.getLinkdatatype())) {
						// 如果存放的LinkDataType是strwhere语句，那么linkdata通过setUserObject得到实例化结果
						//将extendSql也加到userObject中,使穿透出来的单据符合查询条件
						linkdata.setUserObject(nodeXMLData.getLinkdata()+" and PK_GATHERING in (" + extendSql.toString() +") ");
					} else {
						// 如果存放的是billid，那么给linkdata实例赋予billid即可
						linkdata.setBillID(rowVo.getStringValue(nodeXMLData
								.getLinkdata()));
					}
					// 根据待打开节点的nodecode，实例化所需要打开的节点VO
					FuncRegisterVO frVO = SFClientUtil
							.findFRVOFromMenuTree(destFunCode);

					//update chenth 没有权限打开穿透单据时处理
					String pkCorp = _getCorpID();
					if (frVO == null && pkCorp != null && hasPower(pkCorp, destFunCode))
						frVO = FuncRegister.getFuncRegisterVOByCode(destFunCode);
					if (frVO != null)
//						FuncNodeStarter.openDialog(frVO, ILinkType.LINK_TYPE_QUERY,
//								linkdata, this, false, true);
						FuncNodeStarter.openFrame(frVO, ILinkType.LINK_TYPE_QUERY,
							linkdata, this, false);
					else
						MessageDialog.showErrorDlg(this, NCLangRes.getInstance().getStrByID("sysframev5", "UPPsysframev5-000062"), (new StringBuilder()).append(NCLangRes.getInstance().getStrByID("sysframev5", "UPPsysframev5-000095")).append(destFunCode).toString());

				} catch (BusinessException e) {
					// modify by zhourj
					Logger.error("穿透结果", e);
					showHintMessage(e.getMessage());
					return;
				}
				showHintMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("H0","UPPH0-002127")/*@res "穿透成功！"*/);
			} else {
				showHintMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("H0","UPPH10301-000629")/*@res "无对应的穿透信息"*/);
			}
		}
	}
	
	/**
	 * 功能描述：是否有打开节点的权限
	 * @创建者：chenth
	 * @创建时间：2010-7-21 下午02:29:28
	 * @param pkCorp
	 * @param funCode
	 * @return
	 */
	private static boolean hasPower(String pkCorp, String funCode)
	{
		boolean power = false;
		String userId = ClientEnvironment.getInstance().getUser().getPrimaryKey();
		try
		{
			IFuncPower powerservice = (IFuncPower)NCLocator.getInstance().lookup(nc.itf.uap.rbac.function.IFuncPower.class.getName());
			power = powerservice.isPowerByUserFunc(userId, pkCorp, funCode);
		}
		catch (BusinessException e)
		{
			e.printStackTrace();
			Logger.error(e.getMessage(), e);
		}
		return power;
	}

	private String[] getExtendInfoFromPeneInfo(IPeneExtendInfo extendInfo,
			String itemkey) {
		if (extendInfo == null) {
			return null;
		}
		// if (exPeneInfoHash == null) {
		HashMap<String, String[]> exPeneInfoHash = new HashMap<String, String[]>();
		String[][] cloumkeys = extendInfo.getColumKeys();
		String[] exWhereStrs = extendInfo.getExtendsWhereStrings();
		String[] exNodecode = extendInfo.getNodeCodes();
		for (int i = 0; i < exWhereStrs.length; i++) {
			String[] arrayCols = cloumkeys[i];
			for (int j = 0; j < arrayCols.length; j++) {
				String columkey = arrayCols[j];
				if (!exPeneInfoHash.containsKey(columkey)) {
					exPeneInfoHash.put(columkey, new String[] { exWhereStrs[i],
							exNodecode[i] });
				}
			}
		}
		// }
		return (String[]) exPeneInfoHash.get(itemkey);
	}
	
	private void specailPenerate() {
		ReportBaseVO rowVo = getSelectedVO();
		// 如果结算金额不为空，那么穿透到结算单。根据结算单的单据类型和结算单的PK
		// 单据pk
		Object pk_bill = rowVo.getAttributeValue("pk_bill");
		if (pk_bill == null)
			return;
		String whereClause = "select pk_contsettle,pk_billtype from pm_cm_contsettle where pk_cont= '"
				+ pk_bill + "' and isnull(dr,0)=0 ";
		try {
			ReportBaseVO[] reportBaseVO = ReportBO_Client
					.queryVOBySql(whereClause);
			String nodeType = null;
			if (reportBaseVO != null && reportBaseVO.length > 0) {
				if (reportBaseVO[0].getAttributeValue("pk_billtype") == null)
					return;
				nodeType = reportBaseVO[0].getAttributeValue("pk_billtype")
						.toString();
			} else {
				return;
			}
			PMLinkQueryData linkdata = new PMLinkQueryData();
			if (reportBaseVO[0].getAttributeValue("pk_contsettle") == null)
				return;
			linkdata.setBillID(reportBaseVO[0].getAttributeValue(
					"pk_contsettle").toString());
			// 如果存放的是单据类型,那么destFunCode也就是待打开节点code通过单据类型去获取
			String destFunCode = PfUIDataCache.getBillType(nodeType)
					.getNodecode();
			// 根据待打开节点的nodecode，实例化所需要打开的节点VO
			FuncRegisterVO frVO = SFClientUtil
					.findFRVOFromMenuTree(destFunCode);
			FuncNodeStarter.openDialog(frVO, ILinkType.LINK_TYPE_QUERY,
					linkdata, this, false, true);
		} catch (DAOException e1) {
			throw new RuntimeException("DAOException Exception encountered", e1);
		} catch (Exception e) {
			nc.bs.logging.Logger.error(e.getMessage(), e);
		}
	}

	
}