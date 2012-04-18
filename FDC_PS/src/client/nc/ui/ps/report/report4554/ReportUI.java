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
 * @���� zhfa
 * @����ʱ�䣺2007-5-14 ����10:13:37
 * @�޸��ߣ�ssd
 * @�޸�ʱ�䣺2007-5-14 ����10:13:37
 * @˵����H3014554	�����ؿ���ܱ�	nc.ui.ps.report.report4554.ReportUI
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
	 * @�����ߣ�zhfa
	 * @����˵����
	 * @����ʱ�䣺2007-5-14 ����10:13:38
	 * @�޸��ߣ�ssd
	 * @�޸�ʱ�䣺2007-5-14 ����10:13:38
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
		svo.setGroupFldCanNUll(true);// �����е������Ƿ����Ϊ�ա�
		svo.setAsLeafRs(new boolean[] { false });// �����кϲ����Ƿ���Ϊĩ���ڵ��¼��
		svo.setValueFlds(new String[] { "shouldmny","reserve6","balancemny", "getmny", "arrearage" });// ��ֵ��:
		svo.setValueFldTypes(new int[] { IUFTypes.UFD, IUFTypes.UFD,IUFTypes.UFD,IUFTypes.UFD, IUFTypes.UFD });// ��ֵ�е�����:
		svo.setTotalDescOnFld("project");// �ϼ�������
		setSubtotalVO(svo);
		doSubTotal();
	}

	protected void queryByCustomWhereClause(String querySql) throws Exception {
		// ZYT �Զ����ɷ������
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

	/**��
	 * ��ȡ�����Ŀͻ�
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
				if (QueueVO[i].getFieldName().equals(NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0004165")//@res "��Ŀ"
)) {
					headVO.setAttributeValue("pk_project", QueueVO[i].getValue());
				}
				if (QueueVO[i].getFieldName().equals(NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0003246")//@res "���㿪ʼ����"
)) {
					headVO.setAttributeValue("begintime", QueueVO[i].getValue());
				}
				if (QueueVO[i].getFieldName().equals(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-001828")//@res "�����������"
)) {
					headVO.setAttributeValue("endtime", QueueVO[i].getValue());
				}
				if (QueueVO[i].getFieldName().equals(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011015-000149")//@res "¥��"
)) {
					headVO.setAttributeValue("pk_building", QueueVO[i].getValue());
					// flag=true;
				}
				if (QueueVO[i].getFieldName().equals(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000545")//@res "����"
)) {
					headVO.setAttributeValue("pk_house", QueueVO[i].getValue());
				}
				if (QueueVO[i].getFieldName().equals(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3010545-000001")//@res "��Ԫ"
)) {
					headVO.setAttributeValue("vhcell", QueueVO[i].getValue());
				}
			}
		}
	}

	private ReportBaseVO[] getSQL(String querySql) throws Exception {
		String sql = " and 1=1 ";
		ConditionVO[] Condition = getQueryDlg().getConditionVO();
		String gathingstate = null; // ��ʼ����
		String gathingend = null; // ��ֹ����
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
			//�޸���  �Ի���  �޸�ʱ�䣺 2012.3.9  �޸�ԭ�򣺸��� ���� �˷� ����Ϊԭ��Ϣ����ʾ start   
			 
				sql += " and ps_cb_gathering_c.pk_customer not in (select ps_so_changename_odd.pk_customer  from ps_so_changename inner join ps_so_changename_odd  on ps_so_changename.pk_changename =  ps_so_changename_odd.pk_changename  where ps_so_changename.pk_house = crm_bd_house.pk_house and ps_so_changename.vbillstatus = 1) ";
				sql += " and ps_cb_gathering_c.pk_customer not in ( select ps_so_hchange_cust.pk_customer  from ps_so_housechange inner join ps_so_hchange_cust on ps_so_housechange.pk_housechange =  ps_so_hchange_cust.pk_housechange where ps_so_housechange.pk_house_old = crm_bd_house.pk_house  and ps_so_housechange.vbillstatus = 1 ) ";
				sql += " and ps_cb_gathering_c.pk_customer not in ( select ps_so_sign_customer.pk_customer from ps_so_sign inner join  ps_so_sign_customer on ps_so_sign.pk_sign = ps_so_sign_customer.pk_sign where ps_so_sign.pk_house = ps_cb_gathering.pk_house and ps_so_sign.reserve1 =1 ) ";
				sql += " and ps_cb_gathering_c.pk_customer not in (select ps_so_subsc_customer.pk_customer from ps_so_subsc inner join  ps_so_subsc_customer on ps_so_subsc.pk_subsc = ps_so_subsc_customer.pk_subsc where ps_so_subsc.pk_house = ps_cb_gathering.pk_house and ps_so_subsc.reserve1 =1 )";
			// end	�˷�SQL ���Ϲ�ǩԼ���й�������ѯ���Ƿ�Ϊ�˷����˷������鲻�������ɲ鿴��������
		}
		ReportBaseVO[] rstVOs = gathingSQL(gathingstate, gathingend, sql);
		return rstVOs;
	}

	/**
	 * ȡ�Ϲ���ǩԼ��ǩԼ��ǰ������
	 * @ʱ�䣺2008-3-31����01:40:17
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
				" bd_psndoc.psnname  as cussellor  " // add by zhaohf time 2012-03-19   ��ҵ����
			   // + "ps_cmg_customer.vpreferredtel as vtel, " //add by zhaohf time 2012-03-19   �ͻ��绰
			   // + "ps_cmg_customer.vcaddress as vaddress, " //add by zhaohf time 2012-03-19   �ͻ���ַ
			   // + "ps_cmg_customer.pk_customer " //add by zhaohf time 2012-03-19   �ͻ�����
			   // + "ps_bd_fundset.vfsname, " //add by zhaohf time 2012-03-19  ����
			   // + "ps_bd_fundtype.vftcode " //add by zhaohf time 2012-03-19  ��������
				+ " from crm_bd_house "
				+ " inner join ps_cb_gathering on  ps_cb_gathering.pk_house=crm_bd_house.pk_house "
				+ " inner join fdc_bd_operstate on fdc_bd_operstate.pk_operstate = crm_bd_house.pk_situation "
				+ " inner join crm_bd_building on crm_bd_building.pk_building = crm_bd_house.pk_building "
				+ " inner join fdc_bd_project on fdc_bd_project.pk_project = crm_bd_house.pk_project "
				+ " inner join ps_cb_gathering_b on ps_cb_gathering.pk_gathering=ps_cb_gathering_b.pk_gathering "


				+ " left join ps_so_arearepair on crm_bd_house.pk_house=ps_so_arearepair.pk_house and ps_so_arearepair.vbillstatus=1 "
//				+" left join ps_so_subsc on ps_cb_gathering.pk_house=ps_so_subsc.pk_house "
				//add by zhaohf time 2012-03-19  ��ҵ���� start
				+ "  left join ps_cb_gathering_c on ps_cb_gathering.pk_gathering = ps_cb_gathering_c.pk_gathering left join ps_cmg_customer_sellor on ps_cmg_customer_sellor.pk_customer = ps_cb_gathering_c.pk_customer" +
				 " left join bd_psndoc on ps_cmg_customer_sellor.pk_psndoc = bd_psndoc.pk_psndoc "
				+ " left join ps_bd_fundset   on ps_bd_fundset.pk_fundset =  ps_cb_gathering_b.pk_fundset  " //����
				+ " left join ps_bd_fundtype  on ps_bd_fundset.pk_fundtype = ps_bd_fundtype.pk_fundtype " //��������
				//add by zhaohf time 2012-03-19  ��ҵ���� end

				+ " where ps_cb_gathering_b.pk_fundset is not null and isnull(ps_cb_gathering_b.dr,0)=0 and isnull(ps_so_arearepair.dr,0)=0 "
				//���� ֻͳ�Ʒ��� �� ��λ��  ����ͳ��
				+ " and ps_bd_fundtype.vftcode in('01', '02') "//modefied by zhaohf  

				//�����ؿ���ܵ�ͳ�Ʋ���ҪԼ���Ϲ���ǩԼ��״̬ @modified by zhangws
//				+ " and (ps_so_sign.pk_sign is null or (ps_so_sign.vbillstatus='"+IBillStatus.CHECKPASS+"' " +"and isnull(ps_so_sign.dr,0)=0  " +
//						" )) "

				//�����ؿ���ܵ�ͳ�Ʋ���ҪԼ���Ϲ���ǩԼ��״̬
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
		// ZYT �Զ����ɷ������
		if (m_qryDlg == null) {
			m_qryDlg = new ReportQueryDlg(this);
			m_qryDlg.setTempletID(_getCorpID(), _getModelCode(), _getUserID(), null);

		}
		return m_qryDlg;
	}

	/**
	 * @�����ߣ�zhfa
	 * @����˵����
	 * @����ʱ�䣺2007-5-14 ����10:13:38
	 * @�޸��ߣ�ssd
	 * @�޸�ʱ�䣺2007-5-14 ����10:13:38
	 * @override
	 *
	 */
	@Override
	public void setUIAfterLoadTemplate() {
	}

	/**
	 *
	 * @�����ߣ��ɾ�
	 * @����˵����
	 * @����ʱ�䣺2007-12-14 ����03:30:32
	 * @�޸��ߣ�Administrator
	 * @�޸�ʱ�䣺2007-12-14 ����03:30:32
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
		//���ݱ�ͷ�Ľ������ڴ�͸����ʱ���������տ
		ReportBaseVO headVO = (ReportBaseVO)getReportBase().getHeadDataVO();
		String gathingstate = null; // ������ʼ����
		String gathingend = null; // �����ֹ����
		gathingstate = (String)headVO.getAttributeValue("begintime");
		gathingend = (String)headVO.getAttributeValue("endtime");
		//extendSqlΪƴ�յķ��ϱ�ͷ��ѯ������sql��䣬
		//��Ϊonpenerate����������nodeXMLData.getLinkdatatype()��ֵ��linkdata��userObject���ԣ�userObjectΪ��͸�Ĳ�ѯ����
		//���Խ�extendSqlҲ�ӵ�userObject��
		StringBuffer extendSql = new StringBuffer().append("select ps_cb_gathering_b.PK_GATHERING from ps_cb_gathering_b ps_cb_gathering_b  where ps_cb_gathering_b.PK_GATHERING=ps_cb_gathering.PK_GATHERING ");
		if(gathingstate!=null && gathingend!=null){
			extendSql.append(" and ").append(" ps_cb_gathering_b.dchargedate >= '").append(gathingstate).append("' ");
			extendSql.append(" and ").append(" ps_cb_gathering_b.dchargedate <= '").append(gathingend).append("' ");
		}
		// ��ȡ��ȡ����VO
		ReportBaseVO rowVo = getSelectedVO();
		if (rowVo == null) {
			showHintMessage(NCLangRes4VoTransl.getNCLangRes().getStrByID("H0","UPPH0-002277")//@res "���б��в�֧�ִ�͸!"
);
			return;
		}
		String itemkey = getItemKey();
		// add by zhaohf �ͻ���Ϣ��͸ start
		if("customer".equals(itemkey)|| "vtel".equals(itemkey) || "vaddress".equals(itemkey)){
			CmgCustomerVO cvo = new CmgCustomerVO(); 
			cvo.setAttributeValue("pk_customer", rowVo.getAttributeValue("pk_customer"));
			cvo.setFccategory(0);
			new PenerateToCustomer().onPenerate(this, getModuleCode(), cvo);
			return ;
		}
		// add by zhaohf �ͻ���Ϣ��͸ end
		Object settleMny = rowVo.getAttributeValue("nmnyb5");
		if (itemkey.equals("nmnyb5")&&settleMny != null && settleMny.toString().trim().length() > 0) {
			specailPenerate();
		} else {

			PMLinkQueryData linkdata = new PMLinkQueryData();
			String destFunCode;
			// �õ���ǰ����ģ���NodeCode
			String reportNode = getPene2NodeInfo();
			String[] extendInfo = getExtendInfoFromPeneInfo(getExtendInfo(),
					itemkey);
			if (reportNode != null && rowVo.getStringValue(itemkey) != null) {
				// ����Զ�̽ӿ��࣬ͨ��Զ��XML���������XML�ļ����������ݴ���� NodeXMLDataʵ������
				IAnalyzeXML analyzeXMLImpl = NCLocator
						.getInstance().lookup(IPSAnalyzeXML.class);

				NodeXMLData nodeXMLData;
				try {

					// add by zhourj �޸�ʱ�䣺09/01/12 �ɴ�͸����ͬĿ�����
					String penetrateObj = getPenetrateObj();
					if (null == penetrateObj || "".equals(penetrateObj)) {
						nodeXMLData = analyzeXMLImpl.analyzeNodeXML(reportNode,
								itemkey, rowVo, extendInfo);
					} else if ("-1".equals(penetrateObj))// ȡ����ť����
					{
						return;
					} else {
						nodeXMLData = analyzeXMLImpl.analyzeNodeXML(reportNode,
								itemkey, rowVo, extendInfo, penetrateObj);
					}

					if ("billtype".equals(nodeXMLData.getLinktype())) {
						// �����ŵ��ǵ�������,��ôdestFunCodeҲ���Ǵ��򿪽ڵ�codeͨ����������ȥ��ȡ
						destFunCode = PfUIDataCache.getBillType(
								nodeXMLData.getCode()).getNodecode();
					} else {
						// �����ŵ���nodecode����ô��nodecode���Ǵ��򿪽ڵ��code
						destFunCode = nodeXMLData.getCode();
					}
					if ("strwhere".equals(nodeXMLData.getLinkdatatype())) {
						// �����ŵ�LinkDataType��strwhere��䣬��ôlinkdataͨ��setUserObject�õ�ʵ�������
						//��extendSqlҲ�ӵ�userObject��,ʹ��͸�����ĵ��ݷ��ϲ�ѯ����
						linkdata.setUserObject(nodeXMLData.getLinkdata()+" and PK_GATHERING in (" + extendSql.toString() +") ");
					} else {
						// �����ŵ���billid����ô��linkdataʵ������billid����
						linkdata.setBillID(rowVo.getStringValue(nodeXMLData
								.getLinkdata()));
					}
					// ���ݴ��򿪽ڵ��nodecode��ʵ��������Ҫ�򿪵Ľڵ�VO
					FuncRegisterVO frVO = SFClientUtil
							.findFRVOFromMenuTree(destFunCode);

					//update chenth û��Ȩ�޴򿪴�͸����ʱ����
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
					Logger.error("��͸���", e);
					showHintMessage(e.getMessage());
					return;
				}
				showHintMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("H0","UPPH0-002127")/*@res "��͸�ɹ���"*/);
			} else {
				showHintMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("H0","UPPH10301-000629")/*@res "�޶�Ӧ�Ĵ�͸��Ϣ"*/);
			}
		}
	}
	
	/**
	 * �����������Ƿ��д򿪽ڵ��Ȩ��
	 * @�����ߣ�chenth
	 * @����ʱ�䣺2010-7-21 ����02:29:28
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
		// ��������Ϊ�գ���ô��͸�����㵥�����ݽ��㵥�ĵ������ͺͽ��㵥��PK
		// ����pk
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
			// �����ŵ��ǵ�������,��ôdestFunCodeҲ���Ǵ��򿪽ڵ�codeͨ����������ȥ��ȡ
			String destFunCode = PfUIDataCache.getBillType(nodeType)
					.getNodecode();
			// ���ݴ��򿪽ڵ��nodecode��ʵ��������Ҫ�򿪵Ľڵ�VO
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