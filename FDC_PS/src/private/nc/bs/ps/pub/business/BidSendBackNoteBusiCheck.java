package nc.bs.ps.pub.business;
import java.util.Collection;
import java.util.HashMap;

import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.crmbd.pub.BdHouseVO;
import nc.vo.crmbd.pub.BdProjectVO;
import nc.vo.fdc.pub.SafeObject;
import nc.vo.ml.NCLangRes4VoTransl;

import nc.bs.dao.BaseDAO;
import nc.bs.uap.lock.PKLock;
import nc.itf.ps.pub.IPsDataMapping;
import nc.vo.ps.ps3010.SscExtendVO;
import nc.vo.ps.ps3025.SscCampVO;
import nc.vo.ps.ps3515.SoQueueVO;
import nc.vo.ps.ps3520.SoEngageVO;
import nc.vo.ps.ps3525.SoSubscVO;
import nc.vo.ps.ps3530.SoSignVO;
import nc.vo.ps.ps353515.SoArearepairVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;

public class BidSendBackNoteBusiCheck {


	public UFBoolean lockPkForAL(String pks[]) throws BusinessException{
		UFBoolean bLocked = new UFBoolean(false);

		if(pks == null || pks.length == 0){
			//不需要加锁
			return new UFBoolean(true);
		}

		//加锁
		PKLock lock = PKLock.getInstance();
		bLocked = new UFBoolean(lock.addBatchDynamicLock(pks));
		if(!bLocked.booleanValue()) throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("H3H0","UPPH3H0-001108")//@res "存在并发操作，请稍后再试！"
);
		return bLocked;
	}

	/**
	 * 进行并发校验 保存时校验上游单据是否产生改变，如果产生改变，抛出异常
	 * @modifier liuhao
	 * @time 2012-3-30 下午04:27:10
	 * @version NC5.7, HNA_RE
	 * @description 房产签约并发问题
	 */
	public void checkTs(AggregatedValueObject billvo) throws BusinessException {

		BaseDAO dao = new BaseDAO();

		//签约单新增保存时，增加对房产状态的校验
		if(billvo!=null&&billvo.getParentVO()!=null&&billvo.getParentVO() instanceof SoSignVO){
			SoSignVO headvo = (SoSignVO)billvo.getParentVO();
			if(headvo.getPrimaryKey()==null||headvo.getPrimaryKey().length()==0){
				//直接签约 新增时校验房产状态
				if(headvo.getVlastbill()==null || headvo.getVlastbill().length()==0){
					String pk_sellstate = headvo.getPk_sellstate();
					String pk_house = headvo.getPk_house();
					lockPkForAL(new String[]{pk_house});
					BdHouseVO houseVO = (BdHouseVO)dao.retrieveByPK(BdHouseVO.class, pk_house,new String[]{"pk_sellstate"});
					if (houseVO != null && houseVO.getPk_sellstate() != null
							&& pk_sellstate != null
							&& !pk_sellstate.equals(houseVO.getPk_sellstate())) {						
						throw new BusinessException(NCLangRes4VoTransl
								.getNCLangRes().getStrByID("HYPS",
										"UPPHYPS-003250")// @res
															// "房产状态已经发生变化，请刷新后重新制单！"
						);
					}
					// modified by liuhao @2012-03-30 16:22 for HNA_RE: 房产签约并发问题
					Collection<?> signVOClos = dao.retrieveByClause(SoSignVO.class, " isnull(dr,0)=0 and (reserve1 not in (1,2,3,4) or reserve1 is null) and pk_house='"+pk_house+"' ",new String[]{"pk_sign","dr"});
					if(signVOClos!=null && signVOClos.size()>0){
						throw new BusinessException("该房产已签约，无法保存！"
						);
					}
					// end modified by liuhao @2012-03-30 16:24 for HNA_RE: 房产签约并发问题
				}
				UFDateTime ts = headvo.getTs();
				if(ts!=null&&ts.toString().length()>0){
					if(headvo.getVlastbill()!=null && headvo.getVlastbill().length()>0){
						String lastbilltype = headvo.getVlastbill();
						String pk_lastbill = headvo.getPk_lastbill();
						Class<?> clz = null;
						//排号
						if(IPsDataMapping.SO_QUEUE.equals(lastbilltype)){
							clz = SoQueueVO.class;
						}
						//预定
						if(IPsDataMapping.SO_PRECONCERT.equals(lastbilltype)){
							clz = SoEngageVO.class;
						}
						//认购
						if(IPsDataMapping.SO_SUBSCRIBE.equals(lastbilltype)){
							clz = SoSubscVO.class;
						}
						SuperVO srcVO = (SuperVO)dao.retrieveByPK(clz, pk_lastbill);
						if(srcVO!=null&&clz!=null){
							lockPkForAL(new String[]{pk_lastbill});
							Object db_ts = srcVO.getAttributeValue("ts");
							if(db_ts!=null && !db_ts.toString().equals(ts.toString())){
								throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("HYPS","UPPHYPS-003251")//@res "来源数据已发生变化，请重新做单！"
);
							}
						}
					}
				}
			}
		}

		Object ts = billvo.getParentVO().getAttributeValue("reserve5");
		billvo.getParentVO().setAttributeValue("reserve5", null);
		Object vbilltype = billvo.getParentVO().getAttributeValue("pk_billtype");
		if(vbilltype == null || vbilltype.toString().trim().length()==0)
			return ;
		// 当ts为空时，不进行校验（执行保存后vdef10字段值被清空，以后修改保存将不执行校验）
		if (ts == null || ts.toString().trim().length()==0)
			return;
		if(billvo.getParentVO().getAttributeValue("pk_lastbill")==null || billvo.getParentVO().getAttributeValue("pk_lastbill").toString().length()==0){
			SuperVO lVO = null;
			if(vbilltype.toString().trim().equals(IPsDataMapping.SSC_EVAL))
				lVO = (SuperVO) dao.retrieveByPK(SscExtendVO.class, billvo.getParentVO().getAttributeValue("vssname").toString());
			else if(vbilltype.toString().trim().equals(IPsDataMapping.MEM_EFFECT))
				lVO = (SuperVO) dao.retrieveByPK(SscCampVO.class, billvo.getParentVO().getAttributeValue("pk_camp").toString());
			if (lVO == null || !ts.toString().trim().equals(lVO.getAttributeValue("ts").toString().trim()))
				throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-002016")//@res "发生数据并发！\r\n本单据不允许保存！"
);
			return ;
		}
		Object vlastbill = billvo.getParentVO().getAttributeValue("vlastbill");
		if(vlastbill==null || vlastbill.toString().length()==0)
			return ;
		SuperVO lastVO = null;
		if(vlastbill.equals(IPsDataMapping.SO_QUEUE)) {
			lastVO = (SuperVO) dao.retrieveByPK(SoQueueVO.class, billvo.getParentVO().getAttributeValue("pk_lastbill").toString());
		} else if(vlastbill.equals(IPsDataMapping.SO_PRECONCERT))
			lastVO = (SuperVO) dao.retrieveByPK(SoEngageVO.class, billvo.getParentVO().getAttributeValue("pk_lastbill").toString());
		else if(vlastbill.equals(IPsDataMapping.SO_SUBSCRIBE)){
			if(vbilltype.toString().trim().equals(IPsDataMapping.CB_GATHERING))
				lastVO = (SuperVO) dao.retrieveByPK(SoSubscVO.class, billvo.getParentVO().getAttributeValue("reserve4").toString());
			else
				lastVO = (SuperVO) dao.retrieveByPK(SoSubscVO.class, billvo.getParentVO().getAttributeValue("pk_lastbill").toString());
		}
		else if(vlastbill.equals(IPsDataMapping.SO_SIGNING)){
			if(vbilltype.toString().trim().equals(IPsDataMapping.CB_GATHERING))
				lastVO = (SuperVO) dao.retrieveByPK(SoSignVO.class, billvo.getParentVO().getAttributeValue("reserve4").toString());
			else
				lastVO = (SuperVO) dao.retrieveByPK(SoSignVO.class, billvo.getParentVO().getAttributeValue("pk_lastbill").toString());
		}else if(vlastbill.equals(IPsDataMapping.SO_SELLCTRL)){
			if(vbilltype.toString().trim().equals(IPsDataMapping.CB_GATHERING)){
				String curBillType = billvo.getParentVO().getAttributeValue("reserve3").toString().trim();
				if(curBillType.equals(IPsDataMapping.SO_SUBSCRIBE)){
					lastVO = (SuperVO) dao.retrieveByPK(SoSubscVO.class, billvo.getParentVO().getAttributeValue("reserve4").toString());
				}else if(curBillType.equals(IPsDataMapping.SO_SIGNING)){
					lastVO = (SuperVO) dao.retrieveByPK(SoSignVO.class, billvo.getParentVO().getAttributeValue("reserve4").toString());
				}else{
					return ;
				}
			}
		}else{
			Object curBillType = billvo.getParentVO().getAttributeValue("reserve3");
			if(curBillType==null || curBillType.toString().trim().length()==0)
				return ;
			if(curBillType.toString().trim().equals(IPsDataMapping.SO_AREAREPAIR)){
				if(billvo.getParentVO().getAttributeValue("reserve4")==null || billvo.getParentVO().getAttributeValue("reserve4").toString().length()==0)
					return ;
				lastVO = (SuperVO) dao.retrieveByPK(SoArearepairVO.class, billvo.getParentVO().getAttributeValue("reserve4").toString());
			}else{
				return ;
			}
		}
		if (lastVO == null || !ts.toString().trim().equals(lastVO.getAttributeValue("ts").toString().trim()))
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-002016")//@res "发生数据并发！\r\n本单据不允许保存！"
);
	}
}