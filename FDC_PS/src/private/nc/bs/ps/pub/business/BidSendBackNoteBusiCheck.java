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
			//����Ҫ����
			return new UFBoolean(true);
		}

		//����
		PKLock lock = PKLock.getInstance();
		bLocked = new UFBoolean(lock.addBatchDynamicLock(pks));
		if(!bLocked.booleanValue()) throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("H3H0","UPPH3H0-001108")//@res "���ڲ������������Ժ����ԣ�"
);
		return bLocked;
	}

	/**
	 * ���в���У�� ����ʱУ�����ε����Ƿ�����ı䣬��������ı䣬�׳��쳣
	 * @modifier liuhao
	 * @time 2012-3-30 ����04:27:10
	 * @version NC5.7, HNA_RE
	 * @description ����ǩԼ��������
	 */
	public void checkTs(AggregatedValueObject billvo) throws BusinessException {

		BaseDAO dao = new BaseDAO();

		//ǩԼ����������ʱ�����ӶԷ���״̬��У��
		if(billvo!=null&&billvo.getParentVO()!=null&&billvo.getParentVO() instanceof SoSignVO){
			SoSignVO headvo = (SoSignVO)billvo.getParentVO();
			if(headvo.getPrimaryKey()==null||headvo.getPrimaryKey().length()==0){
				//ֱ��ǩԼ ����ʱУ�鷿��״̬
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
															// "����״̬�Ѿ������仯����ˢ�º������Ƶ���"
						);
					}
					// modified by liuhao @2012-03-30 16:22 for HNA_RE: ����ǩԼ��������
					Collection<?> signVOClos = dao.retrieveByClause(SoSignVO.class, " isnull(dr,0)=0 and (reserve1 not in (1,2,3,4) or reserve1 is null) and pk_house='"+pk_house+"' ",new String[]{"pk_sign","dr"});
					if(signVOClos!=null && signVOClos.size()>0){
						throw new BusinessException("�÷�����ǩԼ���޷����棡"
						);
					}
					// end modified by liuhao @2012-03-30 16:24 for HNA_RE: ����ǩԼ��������
				}
				UFDateTime ts = headvo.getTs();
				if(ts!=null&&ts.toString().length()>0){
					if(headvo.getVlastbill()!=null && headvo.getVlastbill().length()>0){
						String lastbilltype = headvo.getVlastbill();
						String pk_lastbill = headvo.getPk_lastbill();
						Class<?> clz = null;
						//�ź�
						if(IPsDataMapping.SO_QUEUE.equals(lastbilltype)){
							clz = SoQueueVO.class;
						}
						//Ԥ��
						if(IPsDataMapping.SO_PRECONCERT.equals(lastbilltype)){
							clz = SoEngageVO.class;
						}
						//�Ϲ�
						if(IPsDataMapping.SO_SUBSCRIBE.equals(lastbilltype)){
							clz = SoSubscVO.class;
						}
						SuperVO srcVO = (SuperVO)dao.retrieveByPK(clz, pk_lastbill);
						if(srcVO!=null&&clz!=null){
							lockPkForAL(new String[]{pk_lastbill});
							Object db_ts = srcVO.getAttributeValue("ts");
							if(db_ts!=null && !db_ts.toString().equals(ts.toString())){
								throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("HYPS","UPPHYPS-003251")//@res "��Դ�����ѷ����仯��������������"
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
		// ��tsΪ��ʱ��������У�飨ִ�б����vdef10�ֶ�ֵ����գ��Ժ��޸ı��潫��ִ��У�飩
		if (ts == null || ts.toString().trim().length()==0)
			return;
		if(billvo.getParentVO().getAttributeValue("pk_lastbill")==null || billvo.getParentVO().getAttributeValue("pk_lastbill").toString().length()==0){
			SuperVO lVO = null;
			if(vbilltype.toString().trim().equals(IPsDataMapping.SSC_EVAL))
				lVO = (SuperVO) dao.retrieveByPK(SscExtendVO.class, billvo.getParentVO().getAttributeValue("vssname").toString());
			else if(vbilltype.toString().trim().equals(IPsDataMapping.MEM_EFFECT))
				lVO = (SuperVO) dao.retrieveByPK(SscCampVO.class, billvo.getParentVO().getAttributeValue("pk_camp").toString());
			if (lVO == null || !ts.toString().trim().equals(lVO.getAttributeValue("ts").toString().trim()))
				throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-002016")//@res "�������ݲ�����\r\n�����ݲ������棡"
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
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-002016")//@res "�������ݲ�����\r\n�����ݲ������棡"
);
	}
}