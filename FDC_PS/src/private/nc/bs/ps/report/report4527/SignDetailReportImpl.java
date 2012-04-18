package nc.bs.ps.report.report4527;
import java.util.List;
import java.util.Map;

import nc.impl.ps.pub.PSAbstractReportQryImp;
import nc.itf.ps.pub.IPsDataMapping;
import nc.ui.querytemplate.querytree.QueryTree;
import nc.vo.crmbd.pub.tools.CommonUtil;
import nc.vo.jzfdc.pub.report.ReportBaseVO;
import nc.vo.jzfdc.pub.report.ReportQryTool;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.ps.pub.ISubscStatus;
import nc.vo.ps.pub.util.ReportBaseUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.trade.pub.IBillStatus;


/**
 * 签约明细表后台实现类
 * @auther：magw 
 * @date：2011-7-21
 **/
public class SignDetailReportImpl extends PSAbstractReportQryImp {

	@Override
	public String[] getOrderColNames() {
		return null;
	}

	@Override
	public String getQuerySql(QueryTree currTree, QueryTree powerTree,Object userObject) {
		
		String defaultWhereSql = getWhereSql(currTree, powerTree, userObject);
		
		StringBuffer signBuffer = new StringBuffer();
		signBuffer.append(" select " 
				+ "fdc_bd_project.vname as project_name, " 
				+ "ps_so_sign.vbargainnum as vbargainnum, " 
				+ "ps_so_sign_discount.vdef1," //zhaohf 公文审批号 
				+ "ps_so_sign.pk_sign, " 
				+ "ps_so_sign.dsigndate, "
				+ "ps_bd_paymode.vpmname as pk_paymode,  "
				+ "ps_cmg_customer.vcname as customer_name,  "
				+ "crm_bd_house.vhname as pk_house,  "
				+ "ps_so_queue.vqueuebargainnum as vfinallybargainnum,  "

				+ "case ps_bd_paymode.fpmtype "
				+ "when 0 then	(select sum(ps_so_course_b.nyfactmny) from ps_so_course_b where ps_so_course_b.pk_course = ps_so_course.pk_course and isnull( ps_so_course_b.dr,0)=0 and (ps_so_course_b.pk_fundset_fund in (select pk_fundset from ps_bd_fundset where vfscode like '" + IPsDataMapping.FUND_HOUSE_FIRST_MONEY_F + "%') or ps_so_course_b.pk_fundset_else in (select pk_fundset from ps_bd_fundset where vfscode like'" + IPsDataMapping.FUND_HOUSE_FIRST_MONEY_F + "%'))) "
				+ "when 1 then (select sum(ps_so_course_b.nyfactmny) from ps_so_course_b where ps_so_course_b.pk_course = ps_so_course.pk_course and isnull( ps_so_course_b.dr,0)=0 and (ps_so_course_b.pk_fundset_fund in (select pk_fundset from ps_bd_fundset where vfscode like '" + IPsDataMapping.FUND_HOUSE_FIRST_MONEY_F + "%') or ps_so_course_b.pk_fundset_else in (select pk_fundset from ps_bd_fundset where vfscode like'" + IPsDataMapping.FUND_HOUSE_FIRST_MONEY_F + "%')))  "
				+ "else 0 end as firstpay,  "
				
				+ "case ps_bd_paymode.fpmtype "
				+ "	when 0 then (select sum(ps_so_course_b.nyfactmny) from ps_so_course_b where ps_so_course_b.pk_course = ps_so_course.pk_course and isnull( ps_so_course_b.dr,0)=0 and (ps_so_course_b.pk_fundset_fund = (select pk_fundset from ps_bd_fundset where vfscode ='" + IPsDataMapping.FUND_HOUSE_LOAN + "') or ps_so_course_b.pk_fundset_else = (select pk_fundset from ps_bd_fundset where vfscode ='" + IPsDataMapping.FUND_HOUSE_LOAN+ "')))  "
				+ "	when 1 then (select sum(ps_so_course_b.nyfactmny) from ps_so_course_b where ps_so_course_b.pk_course = ps_so_course.pk_course and isnull( ps_so_course_b.dr,0)=0 and (ps_so_course_b.pk_fundset_fund = (select pk_fundset from ps_bd_fundset where vfscode ='" + IPsDataMapping.FUND_HOUSE_LOAN + "') or ps_so_course_b.pk_fundset_else = (select pk_fundset from ps_bd_fundset where vfscode ='" + IPsDataMapping.FUND_HOUSE_LOAN + "')))  "
				+ "	else 0 end as mortgage,  "
				
				+ "ps_so_sign.nsignarea as sell_area, " 
				
				+ "case crm_bd_house.fsellmethod  "
				+ " when 0 then  '" + NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0001881")//@res "建筑面积"
				+ "' when 1 then  '" + NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011015-000083")//@res "套内面积"
				+ "' when 2 then  '" + NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000895")//@res "套（个）"
				+ "' else '' end as sell_type, "
				
				+ "fdc_bd_operstate.vname as pk_situation,  "
				+ "ps_so_sign.nypricebeforedis as before_pack_price,  "
				+ "ps_so_sign.nytotalbeforedis as before_pack_total,  "
				+ "ps_so_sign.nypricesign as after_pack_price,  "
				+ "ps_so_sign.nytotalmnysign as after_pack_total,  "
				
				//对于特殊事务的实收都显示为0
				+ "case ps_so_sign.reserve1 when '1' then 0 when '2' then 0 when '3' then 0 when '4' then 0 "
				+ "else (select sum(ps_so_course_b.nyfactmny) from ps_so_course_b " 
				+ "where ps_so_course_b.pk_course = ps_so_course.pk_course and isnull( ps_so_course_b.dr,0)=0 ) end as fact_receive,"

//				+ "(select sum(ps_so_course_b.nyfactmny) from ps_so_course_b where ps_so_course_b.pk_course = ps_so_course.pk_course and isnull( ps_so_course_b.dr,0)=0 ) as fact_receive,  "
				+ " bd_psndoc.psnname," 
				+ "ps_so_sign_customer.pk_customer," 
				+ "case ps_so_sign.reserve1 " 
				+ " when '1' then " + " '"+ ISubscStatus.Special_status_1_name+"' "
				+ " when '2' then " + " '"+ ISubscStatus.Special_status_2_name+"' "
				+ " when '3' then " + " '"+ ISubscStatus.Special_status_3_name+"' "
				+ " when '4' then " + " '"+ ISubscStatus.Special_status_4_name+"' "
				+ " else '"+ ISubscStatus.Subsc_status_1_name+"' end as status ,"  //
				+ " ps_so_sign.vmemo");  //ps_so_sign.vmemo
		
		signBuffer.append(" from ps_so_sign "
				+ " left join ps_so_sign_discount  on ps_so_sign_discount.pk_sign = ps_so_sign.pk_sign "//zhaohf 公文审批号 
				+ " inner join crm_bd_house on ps_so_sign.pk_house = crm_bd_house.pk_house and isnull(crm_bd_house.dr,0)=0 "
				+ " inner join fdc_bd_project on fdc_bd_project.pk_project = crm_bd_house.pk_project and isnull(fdc_bd_project.dr,0)=0 "
				+ " inner join crm_bd_building on crm_bd_building.pk_building = crm_bd_house.pk_building and isnull(crm_bd_building.dr,0)=0 "
				+ " left outer join ps_so_sign_customer on ps_so_sign_customer.pk_sign = ps_so_sign.pk_sign and isnull(ps_so_sign_customer.dr,0)=0 "
				+ " left outer join ps_cmg_customer on ps_cmg_customer.pk_customer = ps_so_sign_customer.pk_customer and isnull(ps_cmg_customer.dr,0)=0 "
				+ " left outer join ps_cmg_customer_sellor on ps_cmg_customer_sellor.pk_customer = ps_so_sign_customer.pk_customer and isnull(ps_cmg_customer_sellor.dr,0)=0 "
				+ " left outer join bd_psndoc on bd_psndoc.pk_psndoc=ps_cmg_customer_sellor.pk_psndoc and isnull(bd_psndoc.dr,0)=0 "
				+ " left outer join ps_bd_paymode on ps_bd_paymode.pk_paymode = ps_so_sign.pk_paymode and isnull(ps_bd_paymode.dr,0)=0 "
				+ " left outer join ps_so_queue on ps_so_queue.pk_queue = ps_so_sign.pk_queue and isnull(ps_so_queue.dr,0)=0 "
				+ " left outer join fdc_bd_operstate on fdc_bd_operstate.pk_operstate = crm_bd_house.pk_situation and isnull(fdc_bd_operstate.dr,0)=0 "
				+ " left outer join crm_bd_sellstate on crm_bd_sellstate.pk_sellstate = crm_bd_house.pk_sellstate and isnull(crm_bd_sellstate.dr,0)=0 "
				+ " left outer join ps_so_course on ps_so_course.pk_sell = ps_so_sign.pk_sign and isnull(ps_so_course.dr,0)=0 ");
		
		signBuffer.append(" where isnull(ps_so_sign.dr,0)=0 ");
		
		if(defaultWhereSql!=null && defaultWhereSql.length()>0){			
			signBuffer.append(defaultWhereSql);
		}
		
		signBuffer.append(" order by crm_bd_house.pk_project,ps_so_sign.reserve1 ");

		return signBuffer.toString();
	}	

	@Override
	public ReportBaseVO[] queryDataByCond(QueryTree currTree,QueryTree powerTree, Object userObj) throws BusinessException {
		
		ReportBaseVO[] rstVOs = super.queryDataByCond(currTree, powerTree, userObj);

		return ReportBaseUtil.getPKlinkname(ReportBaseUtil.getPKlinkname(rstVOs, "pk_sign", "pk_customer", new String[] { "psnname" }), "pk_sign", null, new String[] { "customer_name","psnname" });

	}

	@SuppressWarnings("unchecked")
	@Override
	protected String getWhereSql(QueryTree currTree, QueryTree powerTree, Object userObject) {
		
		String sql = super.getWhereSql(currTree, powerTree, userObject);
		if(sql != null && sql.length()>0){
			sql = sql.replaceAll("dsigndate1", "dsigndate").replaceAll("dsigndate2", "dsigndate");
		}else{
			sql = "";
		}
		
		if(userObject != null && userObject instanceof Map){
			Map<String, Object> uo = (Map<String, Object>)userObject;
			//是否包含未审批单据
			if(uo.get("bonlyapprove")!=null){				
				boolean bonlyapprove = ((UFBoolean)uo.get("bonlyapprove")).booleanValue();
				if (!bonlyapprove){
					sql += " and ps_so_sign.vbillstatus = '" + IBillStatus.CHECKPASS + "' ";
				}
			}
			//只包含直接签约
			if(uo.get("bonlysign")!=null){				
				boolean bonlysign = ((UFBoolean)uo.get("bonlysign")).booleanValue();
				if (bonlysign){
					sql += " and (ps_so_sign.vlastbill is null or ps_so_sign.vlastbill != '946D')";
				}
			}
			List<String> withstatus = ( List<String>)uo.get("withstatus");
			if(withstatus!=null && withstatus.size()>0 ){
				if(withstatus.contains(ISubscStatus.Subsc_status_0)){
					withstatus.remove(0);
					sql += " and (ps_so_sign.reserve1 is null or ps_so_sign.reserve1 = ''";
					if(withstatus!=null && withstatus.size()>0){				
						sql += " or ps_so_sign.reserve1 in ("+CommonUtil.arrayToString(withstatus.toArray(new String[0]))+")";
					}
					sql += ")";
				}else{
					sql += " and ps_so_sign.reserve1 in ("+CommonUtil.arrayToString(withstatus.toArray(new String[0]))+")";
				}
			}
			
			if(uo.get("projectsql")!=null){
				String projectsql = (String) uo.get("projectsql");
				String pk_projectSql = ReportQryTool.getQryTreeNodeWhereSql(currTree, "crm_bd_house.pk_project");
				if(pk_projectSql!=null && pk_projectSql.length()>0){
					sql = sql.replaceAll(pk_projectSql,projectsql) ;
				}
			}

		}
	
		return sql;
	}

}