package nc.ui.ps.report.report4552;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.jzfdc.pub.IAnalyzeXML;
import nc.itf.jzfdc.pub.report.IPeneExtendInfo;
import nc.itf.ps.pub.IDownList;
import nc.itf.ps.pub.IPSAnalyzeXML;
import nc.itf.ps.pub.IPSModuleCode;
import nc.itf.uap.rbac.function.IFuncPower;
import nc.ui.fdc.pub.PmUIProxy;
import nc.ui.fdc.pub.report.ReportBaseUI;
import nc.ui.jzfdc.report.PMLinkQueryData;
import nc.ui.ml.NCLangRes;
import nc.ui.pf.pub.PfUIDataCache;
import nc.ui.ps.pub.PSUIProxy;
import nc.ui.ps.pub.paramreader.ParamReader;
import nc.ui.ps.pub.query.PSReportQueryDLG;
import nc.ui.ps.report.pub.PSReportPubOpreate;
import nc.ui.pub.ClientEnvironment;
import nc.ui.pub.FuncNodeStarter;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.linkoperate.ILinkType;
import nc.ui.sm.power.FuncRegister;
import nc.ui.trade.report.query.QueryDLG;
import nc.ui.uap.sf.SFClientUtil;
import nc.vo.jzfdc.pub.NodeXMLData;
import nc.vo.jzfdc.pub.report.ReportBaseVO;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.ps.ps3525.SoSubscCustomerVO;
import nc.vo.ps.ps3525.SoSubscVO;
import nc.vo.ps.ps3530.SoSignCustomerVO;
import nc.vo.ps.ps3530.SoSignVO;
import nc.vo.ps.pub.CmgCustomerVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.query.ConditionVO;
import nc.vo.sm.funcreg.FuncRegisterVO;
import nc.vo.trade.pub.IBillStatus;

/**
 * @modifier liuhao
 * @time 2012-4-18 ����03:33:53
 * @version NC5.7, HNA_RE
 * @description H3014552	Ƿ����ϸ��	nc.ui.ps.report.report4552.ReportUI
 */
@SuppressWarnings({"unchecked","deprecation", "serial"})
public class ReportUI extends ReportBaseUI {
	// �ͻ�����ҵ���ʵ�Haspmap,��Ϊһ���ͻ����ܶ�Ӧ�����ҵ���ʣ�����value�Ǹ�Arraylist
	Map m_customNewSellor = new HashMap<String, ArrayList<String>>();
	Map m_customOldSellor = new HashMap<String, ArrayList<String>>();
	Map m_houseSellor = new HashMap<String, String[]>();
	/**
	 * key���������� value���ͻ���Ϣ����
	 */
	Map<String, String[]> house_cusinfo = new HashMap<String, String[]>();

	private String m_operator;
	private String m_project;
	private String sdate;
	private String edate;
	private String sellpoint = null;
	private PSReportQueryDLG m_qryDlg = null;

	public String _getModelCode() {
		return IPSModuleCode.DETAIL_LEFTMNY;
	}

	public QueryDLG getQueryDlg() {
		if (m_qryDlg == null) {
			m_qryDlg = new QueryDlg(this);
			m_qryDlg.setTempletID(_getCorpID(), _getModelCode(), _getUserID(), null);
		}
		return m_qryDlg;
	}

	public void onQuery() {
		try {
			getQueryDlg().showModal();
			if (getQueryDlg().getResult() == UIDialog.ID_OK) {
				String querySql = getQueryDlg().getWhereSQL();
				queryByCustomWhereClause(querySql);
			}
		} catch (Exception e) {
			showWarningMessage(e.getMessage());
		}
	}


	private void queryByCustomWhereClause(String querySql) {
		try {
			sdate = null;
			edate = null;
			m_operator = null;
			m_project = "";
			String pk_corp = ClientEnvironment.getInstance().getCorporation().getPk_corp();
			ConditionVO[] conditionVO = getQueryDlg().getConditionVO();
			if (conditionVO == null || conditionVO.length == 0) {
				return;
			}
			String subscsql = null;
			String signsql = null;
			String iscleardis = null;
 
			ReportBaseVO retVO = new ReportBaseVO();
			UFDate logindatetime = ClientEnvironment.getServerTime().getDate();
			for (int i = 0; i < conditionVO.length; i++) {
				if (conditionVO[i].getFieldName().equals(NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0004165")//@res "��Ŀ"
)) {
					retVO.setAttributeValue("pk_project", conditionVO[i].getValue());
					signsql = "and crm_bd_house.pk_project in("
							+ new PSReportPubOpreate().getChildProjPKByWherePart(conditionVO[i].getValue()) + ")";
					subscsql = "and crm_bd_house.pk_project in("
							+ new PSReportPubOpreate().getChildProjPKByWherePart(conditionVO[i].getValue()) + ")";
 
					m_project = "and ps_bd_sellpost.pk_project in("
							+ new PSReportPubOpreate().getChildProjPKByWherePart(conditionVO[i].getValue()) + ")";
					
				} else if (conditionVO[i].getFieldName().equals("����")) {
					retVO.setAttributeValue("pk_house", conditionVO[i].getValue());
					signsql = signsql + " and crm_bd_house.pk_house ='" + conditionVO[i].getValue() + "'";
					subscsql = subscsql + " and crm_bd_house.pk_house ='" + conditionVO[i].getValue() + "'"; 
				} else if (conditionVO[i].getFieldName().equals(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-001791")//@res "���ۿ�ʼ����"
)) {
					retVO.setAttributeValue("sdate", conditionVO[i].getValue());
					sdate = conditionVO[i].getValue();
					signsql = signsql + " and ps_so_sign.dsigndate>='" + sdate + "'";
					subscsql = subscsql + " and ps_so_subsc.dsubscdate>='" + sdate + "'";
 
				} else if (conditionVO[i].getFieldName().equals(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-001792")//@res "���۽�������"
)) {
					retVO.setAttributeValue("edate", conditionVO[i].getValue());
					edate = conditionVO[i].getValue();
					signsql = signsql + " and ps_so_sign.dsigndate<='" + edate + "'";
					subscsql = subscsql + " and ps_so_subsc.dsubscdate<='" + edate + "'";
 
				} else if (conditionVO[i].getFieldName().equals(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3010530-000021")//@res "����"
)) {
					retVO.setAttributeValue("pk_fundset", conditionVO[i].getValue());
					signsql = signsql + " and ps_so_course_b.pk_fundset_fund='" + conditionVO[i].getValue() + "'";
					subscsql = subscsql + " and ps_so_course_b.pk_fundset_fund='" + conditionVO[i].getValue() + "'"; 
 
				} else if (conditionVO[i].getFieldName().equals(NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0002188")//@res "����Ա"
)) {
					retVO.setAttributeValue("operatorid", conditionVO[i].getValue());
					m_operator = conditionVO[i].getValue();
				} else if (conditionVO[i].getFieldName().equals(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-001825")//@res "��������Ƿ���ʾ"
)) {
					retVO.setAttributeValue("iscleardisplay", conditionVO[i].getValue());
					if (!"Y".equals(conditionVO[i].getValue())) {
						signsql = signsql + " and ps_so_course_b.bisfinish<>'Y'";
						subscsql = subscsql + " and ps_so_course_b.bisfinish<>'Y'";
 
					} else {
						iscleardis = NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-001826")//@res "���������Ҫ��ʾ"
;
					}
				} else if (conditionVO[i].getFieldCode().equals("isellpoint")) {
					setSellpoint(conditionVO[i].getValue());
				}else if(conditionVO[i].getFieldCode().equals("dleftdays")) {
					retVO.setAttributeValue("dleftdays", conditionVO[i].getValue());
					//add by zhaohf time 2012-03-13 add ���ڲ�ѯ����
					//Ӧ����ʱ��<=��ǰʱ��-���ڵ�����
					signsql = signsql + " and ps_so_course_b.dbefinishdate<=to_char(sysdate-"+conditionVO[i].getValue()+",'yyyy-mm-dd')";
					subscsql = subscsql + " and ps_so_course_b.dbefinishdate<=to_char(sysdate-"+conditionVO[i].getValue()+",'yyyy-mm-dd')";
				}else  if (conditionVO[i].getFieldCode().equals("pk_house")) {
					retVO.setAttributeValue("pk_house", conditionVO[i].getValue());
					String sql = " and ps_so_course.pk_house='"+conditionVO[i].getValue()+"'";
					signsql += sql;
					subscsql += sql ; 
				}else if (conditionVO[i].getFieldCode().equals("pk_situation")) {
					retVO.setAttributeValue("pk_situation", conditionVO[i].getValue());  
					String sql = "  and crm_bd_house.pk_situation='"+conditionVO[i].getValue()+"'";
					signsql += sql;
					subscsql += sql ; 
				}else if (conditionVO[i].getFieldCode().equals("pk_customer_sellor")) {
					retVO.setAttributeValue("pk_customer_sellor", conditionVO[i].getValue()); 
					String sql = "  and ps_cmg_customer_sellor.pk_psndoc='"+conditionVO[i].getValue()+"'";
					signsql += sql;
					subscsql += sql ; 
				}
				//end----------------------------
			}
			if (iscleardis == null || iscleardis.trim().length() == 0) {
				signsql = signsql + " and ps_so_course_b.bisfinish<>'Y'";
				subscsql = subscsql + " and ps_so_course_b.bisfinish<>'Y'";
			}
			signsql = signsql +" and ps_so_course_b.dbefinishdate <'"+logindatetime+"'";
			subscsql = subscsql +" and ps_so_course_b.dbefinishdate <'"+logindatetime+"'"; 
			getReportBase().setHeadDataVO(retVO);
			// ȡ���۳ɽ��ο���
			if (getSellpoint() == null || getSellpoint().trim().length() == 0) {
				String barPoint = ParamReader.getParamter(this.getCorpPrimaryKey().trim(),
						ParamReader.BD_PS_BARGAIN_POINT).trim();
				if (barPoint == null || barPoint.trim().length() == 0) {
					setSellpoint(IDownList.ISELLPOINT[0]);
				} else {
					setSellpoint(barPoint);
				}
			}

			String subsc = "select crm_bd_house.pk_house,crm_bd_house.vhname,ps_bd_fundset.vfsname,ps_so_subsc.nsalearea as nsellarea,ps_so_subsc.ntotalafterdis as nsellmny, "
					+ " crm_bd_house.nsellarea as nfinalarea,isnull(crm_bd_house.nbalancemny,ps_so_subsc.nypriceafterdis) as nfinalmny,ps_so_subsc.pk_subsc as pk_bill,'subsc' as type, "
					+ " ps_so_course_b.nshouldmny,ps_so_course_b.nfactmny,ps_so_course_b.dbefinishdate" 
					//add by zhaohf time 2012-03-13 ҵ̬����,��ҵ���ʣ��ͻ����ƣ��ͻ���ַ���绰���ʱ� start
					+ ",fdc_bd_operstate.vname as situationname," 
					+ "bd_psndoc.psnname as nsellor, "
					+ "ps_cmg_customer.vcname as customername,"
					+ "ps_cmg_customer.vpreferredtel as vtel,"
				    + "ps_cmg_customer.vcaddress as vaddress,"
				    + "ps_cmg_customer.ccpostalcode as vpostcode"
				    //add by zhaohf time 2012-03-13 end 
					+  " from ps_so_subsc "
					+ " inner join ps_so_course on ps_so_subsc.pk_subsc=ps_so_course.pk_sell "
					+ " inner join crm_bd_house on ps_so_subsc.pk_house=crm_bd_house.pk_house "
					+ " left join ps_so_course_b on ps_so_course.pk_course=ps_so_course_b.pk_course "
					+ " inner join ps_bd_fundset on ps_so_course_b.pk_fundset_fund=ps_bd_fundset.pk_fundset "
					// add by zhaohf time 2012-03-16  �Ż���ҵ���ʲ�ѯ  start
					+ " left join fdc_bd_operstate on fdc_bd_operstate.pk_operstate = crm_bd_house.pk_situation " //add by zhaohf time 2012-03-13 ����ҵ̬
					+ " left join  ps_so_subsc_customer "
				    + " on ps_so_subsc_customer.pk_subsc = ps_so_subsc.pk_subsc "
				    + " left join  ps_cmg_customer_sellor "
				    + " on ps_cmg_customer_sellor.pk_customer = ps_so_subsc_customer.pk_customer "
				    + "  left join ps_cmg_customer"  
				    + "  on ps_cmg_customer.pk_customer = ps_cmg_customer_sellor.pk_customer "
				    + " left join bd_psndoc "
				    + " on ps_cmg_customer_sellor.pk_psndoc=bd_psndoc.pk_psndoc"
				   // add by zhaohf time 2012-03-16  �Ż���ҵ���ʲ�ѯ  end
					+ " where ps_so_subsc.vbillstatus=1 and ps_so_course_b.bisfund='Y' and isnull(ps_so_subsc.dr,0)=0 and isnull(ps_so_course.dr,0)=0 and isnull(ps_so_course_b.dr,0)=0 "
					+ " and (ps_so_subsc.reserve1 is null or ps_so_subsc.reserve1='')"
					// add by zhaohf time 2012-03-16  �Ż���ҵ���ʲ�ѯ  start
					+ " and isnull(ps_cmg_customer_sellor.dr,0)=0 and isnull(bd_psndoc.dr,0)=0 "
					+ " and ps_cmg_customer_sellor.bcsellornow='Y' "
					+ " and ps_cmg_customer.pk_customer not in( "
					+ " select ps_so_changename_odd.pk_customer from ps_so_changename  inner join ps_so_changename_odd " 
					+ " on ps_so_changename.pk_changename = ps_so_changename_odd.pk_changename "
					+ " where ps_so_changename.pk_house = crm_bd_house.pk_house "
					+ " and ps_so_changename.vbillstatus = 1 ) "
					// add by zhaohf time 2012-03-16  �Ż���ҵ���ʲ�ѯ  end
					//	��Ҫȥ����תǩԼ����ǩԼ������ͨ��
					+ " and not exists (select 1 from ps_so_sign where ( isnull ( ps_so_sign.dr , 0 ) = 0 ) and ps_so_sign.pk_lastbill=ps_so_subsc.pk_subsc and ps_so_sign.vlastbill=ps_so_subsc.pk_billtype and ps_so_sign.vbillstatus='" + IBillStatus.CHECKPASS + "')"
					+ subscsql;

			String sign = "select crm_bd_house.pk_house,crm_bd_house.vhname,ps_bd_fundset.vfsname,ps_so_sign.nsignarea as nsellarea,ps_so_sign.ntotalmnysign as nsellmny, "
					+ " crm_bd_house.nsellarea as nfinalarea,isnull(crm_bd_house.nbalancemny,ps_so_sign.ntotalmnysign) as nfinalmny,ps_so_sign.pk_sign as pk_bill,'sign' as type, "
					+ " ps_so_course_b.nshouldmny,ps_so_course_b.nfactmny,ps_so_course_b.dbefinishdate" 
					//add by zhaohf time 2012-03-13 ҵ̬����,��ҵ���ʣ��ͻ����ƣ��ͻ���ַ���绰���ʱ� start
					+ ",fdc_bd_operstate.vname as situationname," 
					+ "bd_psndoc.psnname as nsellor, "
					+ "ps_cmg_customer.vcname as customername,"
					+ "ps_cmg_customer.vpreferredtel as vtel,"
				    + "ps_cmg_customer.vcaddress as vaddress,"
				    + "ps_cmg_customer.ccpostalcode as vpostcode"
				    //add by zhaohf time 2012-03-13 end 
					+ " from ps_so_sign "
					+ " inner join ps_so_course on ps_so_sign.pk_sign=ps_so_course.pk_sell "
					+ " inner join crm_bd_house on ps_so_sign.pk_house=crm_bd_house.pk_house "
					+ " left join ps_so_course_b on ps_so_course.pk_course=ps_so_course_b.pk_course "
					+ " inner join ps_bd_fundset on ps_so_course_b.pk_fundset_fund=ps_bd_fundset.pk_fundset "
					// add by zhaohf time 2012-03-16  �Ż���ҵ���ʲ�ѯ  start
					+ " left join fdc_bd_operstate on fdc_bd_operstate.pk_operstate = crm_bd_house.pk_situation " //add by zhaohf time 2012-03-13 ����ҵ̬
					+ " inner join  ps_so_sign_customer "
				    + " on ps_so_sign_customer.pk_sign = ps_so_sign.pk_sign "
				    + " left join  ps_cmg_customer_sellor "
				    + " on ps_cmg_customer_sellor.pk_customer = ps_so_sign_customer.pk_customer "
				    + "  left join ps_cmg_customer on ps_cmg_customer.pk_customer = ps_cmg_customer_sellor.pk_customer "
				    + " inner join bd_psndoc "
				    + " on ps_cmg_customer_sellor.pk_psndoc=bd_psndoc.pk_psndoc"
				   // add by zhaohf time 2012-03-16  �Ż���ҵ���ʲ�ѯ  end
					+ " where ps_so_sign.vbillstatus=1 and ps_so_course_b.bisfund='Y' and isnull(ps_so_sign.dr,0)=0 and isnull(ps_so_course.dr,0)=0 and isnull(ps_so_course_b.dr,0)=0 "
					+ " and (ps_so_sign.reserve1 is null or ps_so_sign.reserve1='')"
					// add by zhaohf time 2012-03-16  �Ż���ҵ���ʲ�ѯ  start
					+ " and isnull(ps_cmg_customer_sellor.dr,0)=0 and isnull(bd_psndoc.dr,0)=0 "
					+ " and ps_cmg_customer_sellor.bcsellornow='Y' "
					+ " and ps_cmg_customer.pk_customer not in( "
					+ " select ps_so_changename_odd.pk_customer from ps_so_changename  inner join ps_so_changename_odd " 
					+ " on ps_so_changename.pk_changename = ps_so_changename_odd.pk_changename "
					+ " where ps_so_changename.pk_house = crm_bd_house.pk_house "
					+ " and ps_so_changename.vbillstatus = 1 ) "
					// add by zhaohf time 2012-03-16  �Ż���ҵ���ʲ�ѯ  end
					+ signsql; 
			ReportBaseVO[] allVOs = null;
			if (getSellpoint().equals(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000153")//@res "�Ϲ�"
)) {
				ReportBaseVO[] dbaseVO1 = PmUIProxy.getIReportService().queryVOBySql(subsc);
				ReportBaseVO[] dbaseVO2 = PmUIProxy.getIReportService().queryVOBySql(sign);
				allVOs = new ReportBaseVO[dbaseVO1.length + dbaseVO2.length];
				System.arraycopy(dbaseVO1, 0, allVOs, 0, dbaseVO1.length);
				System.arraycopy(dbaseVO2, 0, allVOs, dbaseVO1.length, dbaseVO2.length);
			} else {
				allVOs = PmUIProxy.getIReportService().queryVOBySql(sign);
			}
			ArrayList<ReportBaseVO> arrayList = new ArrayList<ReportBaseVO>();
			ReportBaseVO[] baseVOs  = null;
			if (m_operator != null && m_operator.trim().length() > 0) {
				String operatorSql = "select ps_bd_sellpost.fright,bd_psndoc.psnname from ps_bd_sellpost_b "
					+ " inner join bd_psndoc on ps_bd_sellpost_b.pk_psndoc=bd_psndoc.pk_psndoc "
					+ " inner join bd_psnbasdoc on bd_psnbasdoc.pk_psnbasdoc=bd_psndoc.pk_psnbasdoc "
					+ " inner join ps_bd_sellpost on ps_bd_sellpost.pk_sellpost=ps_bd_sellpost_b.pk_sellpost "
					+ " inner join sm_userandclerk on sm_userandclerk.pk_psndoc=bd_psnbasdoc.pk_psnbasdoc "
					+ " where bd_psndoc.pk_corp='"
					+ pk_corp
					+ "' and sm_userandclerk.userid='"
					+ m_operator + "' " + m_project;
				baseVOs = PmUIProxy.getIReportService().queryVOBySql(operatorSql);
			}
			boolean isSeeAll = false;
			if (m_operator != null && m_operator.trim().length() > 0) {
				if (baseVOs != null && baseVOs.length > 0) {
					if (baseVOs[0].getAttributeValue("fright").toString().equals("2")) {
						isSeeAll = true;
					}
				} else {
					isSeeAll = true;
				}
			} else {
				isSeeAll = true;
			}

			if (allVOs != null && allVOs.length > 0) {
				for (int i = 0; i < allVOs.length; i++) {
					if (allVOs[i].getAttributeValue("pk_house") != null
							&& allVOs[i].getAttributeValue("pk_house").toString().trim().length() > 0) {
						//ע���ˣ� �Ի��壬�������Ƿ�Ҫ��ȫ�����������SQL���Ѿ�ȫ���������Ч��������� start
						/*String[] sellors = getSellor(allVOs[i].getAttributeValue("pk_bill").toString(),
								allVOs[i].getAttributeValue("type").toString())[0].toString().split(",");
						for (String string : sellors) {
						}*/
						if (baseVOs!=null && baseVOs.length>0&&allVOs[i].getAttributeValue("customername").equals(baseVOs[0].getAttributeValue("psnname").toString())) {
							isSeeAll = true;
						}
						//ע���ˣ� �Ի��壬�������Ƿ�Ҫ��ȫ�����������SQL���Ѿ�ȫ���������Ч��������� end
						if (isSeeAll) {
						//ע���ˣ� �Ի��壬�������Ƿ�Ҫ��ȫ�����������SQL���Ѿ�ȫ���������Ч��������� end
							/*String[] strcus = getCustomersByHouse(allVOs[i].getAttributeValue("pk_house").toString());
							if (strcus != null && strcus.length > 0) {
								allVOs[i].setAttributeValue("customername", strcus[0]);
								allVOs[i].setAttributeValue("vtel", strcus[1]);
								allVOs[i].setAttributeValue("vaddress", strcus[2]);
								allVOs[i].setAttributeValue("vpostcode", strcus[3]);
							}
							allVOs[i].setAttributeValue("nsellor", getSellor(allVOs[i].getAttributeValue("pk_bill")
									.toString(), allVOs[i].getAttributeValue("type").toString())[0]);
							*/
							//ע���ˣ� �Ի��壬�������Ƿ�Ҫ��ȫ�����������SQL���Ѿ�ȫ���������Ч���������  end
							UFDate logindate = ClientEnvironment.getServerTime().getDate();
							double leftmny = Double.parseDouble(allVOs[i].getAttributeValue("nshouldmny").toString())
									- Double.parseDouble(allVOs[i].getAttributeValue("nfactmny").toString());
							if(allVOs[i].getStringValue("dbefinishdate")!=null){
								if (logindate.getDaysAfter(UFDate.getDate(allVOs[i].getAttributeValue("dbefinishdate")
										.toString())) > 0
										&& leftmny > 0){
									int days = logindate.getDaysAfter(UFDate.getDate(allVOs[i].getAttributeValue("dbefinishdate").toString()));
									allVOs[i].setAttributeValue("nleftday", days + NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-001827"));//@res "��"
									allVOs[i].setAttributeValue("nothermny",days * 10);
								}			
									
							}
							arrayList.add(allVOs[i]);
						} 
					}
					
				}
				ReportBaseVO[] reportBaseVOs = new ReportBaseVO[arrayList.size()];
				arrayList.toArray(reportBaseVOs);
				setBodyVO(reportBaseVOs);
			} else {
				setBodyVO(null);
			}

		} catch (Exception e) {
			nc.bs.logging.Logger.error(e.getMessage(), e);

		}

	}

	/**
	 * ���ݷ�����ȡԭ��ҵ���ʻ�������ҵ������ȡ�ͻ��ӱ�
	 *
	 * @param pk_house
	 * @param status
	 * @return
	 */
	private String[] getSellor(String pk_bill, String type) throws Exception {
		if (m_houseSellor.get(pk_bill) != null)
			return (String[]) m_houseSellor.get(pk_bill);
		Map newSellorMap = new HashMap<String, String>();
		Map oldSellorMap = new HashMap<String, String>();
		String newSellor = "";
		String oldSellor = "";
		if (type.equals("sign")) {
			String strSignWhere = " ps_so_sign_customer.pk_sign='" + pk_bill + "'  ";
			SoSignCustomerVO[] signCustomerVOs = (SoSignCustomerVO[]) PSUIProxy.getIUifService().queryByCondition(
					SoSignCustomerVO.class, strSignWhere);
			if (signCustomerVOs == null || signCustomerVOs.length == 0)
				return new String[] { "", "" };
			for (int i = 0; i < signCustomerVOs.length; i++) {
				ArrayList<ArrayList> sellorlist = getSell(signCustomerVOs[i].getPk_customer());
				ArrayList<String> newsellorlist = sellorlist.get(0);
				for (String string : newsellorlist) {
					if (newSellorMap.get(string) == null) {
						newSellorMap.put(string, string);
					}
				}
				ArrayList<String> oldsellorlist = sellorlist.get(1);
				for (String string2 : oldsellorlist) {
					if (oldSellorMap.get(string2) == null) {
						oldSellorMap.put(string2, string2);
					}
				}
			}
			Iterator it1 = newSellorMap.keySet().iterator();
			while (it1.hasNext()) {
				if (newSellor == null || newSellor.trim().length() == 0) {
					newSellor += newSellorMap.get(it1.next());
				} else {
					newSellor += "," + newSellorMap.get(it1.next());
				}
			}
			Iterator it2 = oldSellorMap.keySet().iterator();
			while (it2.hasNext()) {
				if (oldSellor == null || oldSellor.trim().length() == 0) {
					oldSellor += oldSellorMap.get(it2.next());
				} else {
					oldSellor += "," + oldSellorMap.get(it2.next());
				}
			}
		} else {
			String strubscWhere = " ps_so_subsc_customer.pk_subsc='" + pk_bill
					+ "'  ";
			SoSubscCustomerVO[] signCustomerVOs = (SoSubscCustomerVO[]) PSUIProxy.getIUifService().queryByCondition(
					SoSubscCustomerVO.class, strubscWhere);
			if (signCustomerVOs == null || signCustomerVOs.length == 0)
				return new String[] { "", "" };
			for (int i = 0; i < signCustomerVOs.length; i++) {
				ArrayList<ArrayList> sellorlist = getSell(signCustomerVOs[i].getPk_customer());
				ArrayList<String> newsellorlist = sellorlist.get(0);
				for (String string : newsellorlist) {
					if (newSellorMap.get(string) == null) {
						newSellorMap.put(string, string);
					}
				}
				ArrayList<String> oldsellorlist = sellorlist.get(1);
				for (String string2 : oldsellorlist) {
					if (oldSellorMap.get(string2) == null) {
						oldSellorMap.put(string2, string2);
					}
				}
			}
			Iterator it1 = newSellorMap.keySet().iterator();
			while (it1.hasNext()) {
				if (newSellor == null || newSellor.trim().length() == 0) {
					newSellor += newSellorMap.get(it1.next());
				} else {
					newSellor += "," + newSellorMap.get(it1.next());
				}
			}
			Iterator it2 = oldSellorMap.keySet().iterator();
			while (it2.hasNext()) {
				if (oldSellor == null || oldSellor.trim().length() == 0) {
					oldSellor += oldSellorMap.get(it2.next());
				} else {
					oldSellor += "," + oldSellorMap.get(it2.next());
				}
			}
		}
		m_houseSellor.put(pk_bill, new String[] { newSellor, oldSellor });
		m_customOldSellor.put(pk_bill, new String[] { newSellor, oldSellor });
		return new String[] { newSellor, oldSellor };
	}

	/**
	 * ���ݿͻ��õ���ҵ���ʣ����������customerMap��
	 *
	 * @param pkcustomer
	 * @throws Exception
	 */
	private ArrayList<ArrayList> getSell(String pkcustomer) throws Exception {
		// ���ӳ���ϵ���Ѿ��д˿ͻ�����ô����
		if (m_customNewSellor.get(pkcustomer) != null || m_customOldSellor.get(pkcustomer) != null) {
			ArrayList<ArrayList> alal = new ArrayList<ArrayList>();
			ArrayList<String> newdal = new ArrayList<String>();
			ArrayList<String> oldal = new ArrayList<String>();
			if (m_customNewSellor.get(pkcustomer) != null) {
				newdal = (ArrayList<String>) m_customNewSellor.get(pkcustomer);
			}
			if (m_customOldSellor.get(pkcustomer) != null) {
				oldal = (ArrayList<String>) m_customOldSellor.get(pkcustomer);
			}
			alal.add(newdal);
			alal.add(oldal);
			return alal;
		}
		String whereString = " select bd_psndoc.psnname,ps_cmg_customer_sellor.bcsellornow from bd_psndoc  inner join ps_cmg_customer_sellor  on ps_cmg_customer_sellor.pk_psndoc=bd_psndoc.pk_psndoc "
				+ " and ps_cmg_customer_sellor.pk_customer='"
				+ pkcustomer
				+ "' and isnull(ps_cmg_customer_sellor.dr,0)=0 and isnull(bd_psndoc.dr,0)=0 ";
		ReportBaseVO[] psndocVOs = PmUIProxy.getIReportService().queryVOBySql(whereString);
		if (psndocVOs == null || psndocVOs.length == 0)
			return null;
		ArrayList<ArrayList> alal = new ArrayList<ArrayList>();
		ArrayList<String> newdal = new ArrayList<String>();
		ArrayList<String> oldal = new ArrayList<String>();
		for (int j = 0; j < psndocVOs.length; j++) {
			if ("Y".equalsIgnoreCase(psndocVOs[j].getAttributeValue("bcsellornow").toString())) {
				if (!newdal.contains(psndocVOs[j].getAttributeValue("psnname").toString())) {
					newdal.add(psndocVOs[j].getAttributeValue("psnname").toString());
				}
			} else {
				if (!oldal.contains(psndocVOs[j].getAttributeValue("psnname").toString())) {
					oldal.add(psndocVOs[j].getAttributeValue("psnname").toString());
				}
			}
		}
		m_customNewSellor.put(pkcustomer, newdal);
		m_customOldSellor.put(pkcustomer, oldal);
		alal.add(newdal);
		alal.add(oldal);
		return alal;
	}

	/**
	 *
	 * @�����ߣ��ɾ�
	 * @����˵�������ݷ���ȡ�ÿͻ�
	 * @����ʱ�䣺2007-12-4 ����10:38:40
	 * @�޸��ߣ�ChengJun
	 * @�޸�ʱ�䣺2007-12-4 ����10:38:40
	 * @param pk_house
	 * @return
	 *
	 */
	private String[] getCustomersByHouse(String pk_house) {

		if(pk_house!=null && house_cusinfo.containsKey(pk_house))
			return house_cusinfo.get(pk_house);

		String customers[] = new String[4];
		try {

			SoSignVO[] soSignVOs = (SoSignVO[]) PSUIProxy.getIUifService().queryByCondition(SoSignVO.class,
					"pk_house ='" + pk_house + "' and isnull(dr,0)=0 and (reserve1 is null or reserve1='') ");
			if (soSignVOs != null && soSignVOs.length > 0) {
				SoSignCustomerVO[] soSignCustomerVOs = (SoSignCustomerVO[]) PSUIProxy.getIUifService()
						.queryByCondition(SoSignCustomerVO.class,
								"pk_sign='" + soSignVOs[0].getPrimaryKey() + "' and isnull(dr,0)=0 ");
				customers = getCustomerName(soSignCustomerVOs[0].getPk_customer());
				for (int i = 1; i < soSignCustomerVOs.length; i++) {
					String[] cusArr = getCustomerName(soSignCustomerVOs[i].getPk_customer());
					String cusname = cusArr[0];
					if (cusname != null && cusname.trim().length() > 0 && !customers.equals(cusname)) {
						customers[0] += "," + cusname;
						customers[1] += "," + cusArr[1];
						customers[2] += "," + cusArr[2];
						customers[3] += "," + cusArr[3];
					}
				}


			} else {
				SoSubscVO[] soSubscVOs = (SoSubscVO[]) PSUIProxy.getIUifService().queryByCondition(SoSubscVO.class,
						"pk_house ='" + pk_house + "' and isnull(dr,0)=0 and (reserve1 is null or reserve1='') "+" and not exists (" +
						"select 1 from ps_so_sign where " +
						"ps_so_sign.vlastbill=ps_so_subsc.pk_billtype" +
						" and ps_so_sign.pk_lastbill=ps_so_subsc.pk_subsc and len(ps_so_sign.reserve1)>0)");
				if (soSubscVOs != null && soSubscVOs.length != 0) {
					SoSubscCustomerVO[] soSubscCustomerVOs = (SoSubscCustomerVO[]) PSUIProxy.getIUifService()
							.queryByCondition(SoSubscCustomerVO.class,
									"pk_subsc='" + soSubscVOs[0].getPrimaryKey() + "' and isnull(dr,0)=0 ");
					customers = getCustomerName(soSubscCustomerVOs[0].getPk_customer());
					for (int i = 1; i < soSubscCustomerVOs.length; i++) {
						String[] cusArr = getCustomerName(soSubscCustomerVOs[i].getPk_customer());
						String cusname = cusArr[0];
						if (cusname != null && cusname.trim().length() > 0 && !customers.equals(cusname)) {
							customers[0] += "," + cusname;
							customers[1] += "," + cusArr[1];
							customers[2] += "," + cusArr[2];
							customers[3] += "," + cusArr[3];
						}
					}
				}
			}
		} catch (Exception e) {
			nc.bs.logging.Logger.error(e.getMessage(), e);
		}

		house_cusinfo.put(pk_house, customers);

		return customers;
	}

	private String[] getCustomerName(String pk_customer) throws Exception {
		if (pk_customer == null)
			return new String[] { "", "", "", "" };
		CmgCustomerVO cmgCustomerVO = (CmgCustomerVO) PSUIProxy.getIUifService().queryByPrimaryKey(CmgCustomerVO.class,
				pk_customer);
		if (cmgCustomerVO != null) {
			return new String[] { cmgCustomerVO.getVcname(),
					cmgCustomerVO.getVpreferredtel() == null ? "" : cmgCustomerVO.getVpreferredtel(),
					cmgCustomerVO.getVcaddress() == null ? "" : cmgCustomerVO.getVcaddress(),
					cmgCustomerVO.getCcpostalcode() == null ? "" : cmgCustomerVO.getCcpostalcode() };
		} else
			return new String[] { "", "", "", "" };
	}

	public String getSellpoint() {
		return sellpoint;
	}

	public void setSellpoint(String sellpoint) {
		this.sellpoint = sellpoint;
	}

	public void setUIAfterLoadTemplate() {

	}

	@Override
	public boolean isPage() {
		return false;
	}
	
	/**
	 * ��ǰ��������
	 * ��/modules/fdc_ps/config/penetrate/penetrate.xml�ļ���
	 * ������������NODE<Node code="H3014554">���������������ŵ���������͸
	 * zhaohf
	 */
	public String getPene2NodeInfo() {
		return "H3014554";//�˴�û���������ƣ������˷����ؿ���ܱ������
	}
	/**
	 * ��͸ʵ�ַ���
	 * zhaohf
	 * time 2012-03-14
	 */
	@Override
	public void onPenerate() {
		//���ݱ�ͷ�Ľ������ڴ�͸����ʱ���������տ
		ReportBaseVO headVO = (ReportBaseVO)getReportBase().getHeadDataVO(); 
		 
		//extendSqlΪƴ�յķ��ϱ�ͷ��ѯ������sql��䣬
		//��Ϊonpenerate����������nodeXMLData.getLinkdatatype()��ֵ��linkdata��userObject���ԣ�userObjectΪ��͸�Ĳ�ѯ����
		//���Խ�extendSqlҲ�ӵ�userObject��
		StringBuffer extendSql = new StringBuffer().append("select ps_cb_gathering_b.PK_GATHERING from ps_cb_gathering_b ps_cb_gathering_b  where ps_cb_gathering_b.PK_GATHERING=ps_cb_gathering.PK_GATHERING ");
		 
		// ��ȡ��ȡ����VO
		ReportBaseVO rowVo = getSelectedVO();
		if (rowVo == null) {
			showHintMessage(NCLangRes4VoTransl.getNCLangRes().getStrByID("H0","UPPH0-002277")//@res "���б��в�֧�ִ�͸!"
					        );
			return;
		}
		//�õ�˫�������ֶ�ʱ���ֶ�����
		String itemkey = getItemKey();
		
		//�˴��õ������еķ����ֶ�����/modules/fdc_ps/config/penetrate/penetrate.xml������Ӧ������
		if("vhname".equals(itemkey)){
			itemkey = "housename";//���ڴ˴�ֻ�����˷��������Խ����ֽ���ͳһ�����ڵ�����������pk_house�ֶΡ�
		} 
		PMLinkQueryData linkdata = new PMLinkQueryData();
		String destFunCode;
		// �õ���ǰ����ģ���NodeCode
		String reportNode = getPene2NodeInfo();
		String[] extendInfo = getExtendInfoFromPeneInfo(getExtendInfo(), itemkey);
		if (reportNode != null && rowVo.getStringValue("vhname") != null) {
			// ����Զ�̽ӿ��࣬ͨ��Զ��XML���������XML�ļ����������ݴ���� NodeXMLDataʵ������
			IAnalyzeXML analyzeXMLImpl = NCLocator .getInstance().lookup(IPSAnalyzeXML.class);

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
					linkdata.setBillID(rowVo.getStringValue(nodeXMLData .getLinkdata()));
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
	/**
	 * zhaohf
	 * @param extendInfo
	 * @param itemkey
	 * @return
	 */
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
	/**
	 * zhaohf
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
}