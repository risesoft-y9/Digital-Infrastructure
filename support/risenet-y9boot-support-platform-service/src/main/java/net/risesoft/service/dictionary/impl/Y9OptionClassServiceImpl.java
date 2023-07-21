package net.risesoft.service.dictionary.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9OptionClass;
import net.risesoft.manager.dictionary.Y9OptionValueManager;
import net.risesoft.repository.Y9OptionClassRepository;
import net.risesoft.service.dictionary.Y9OptionClassService;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service
@RequiredArgsConstructor
public class Y9OptionClassServiceImpl implements Y9OptionClassService {

    private final Y9OptionClassRepository y9OptionClassRepository;

    private final Y9OptionValueManager y9OptionValueManager;

    @Transactional(readOnly = false)
    public void createOptionClass(String className, String type) {
        Y9OptionClass option = y9OptionClassRepository.findByName(className);
        if (option == null) {
            option = new Y9OptionClass();
            option.setName(className);
            option.setType(type);
            y9OptionClassRepository.save(option);
            creatOptionValue(type);
        }
    }

    public void creatOptionValue(String type) {
        if ("duty".equals(type)) {
            y9OptionValueManager.create("001A", "书记", type);
            y9OptionValueManager.create("001B", "副书记", type);
            y9OptionValueManager.create("002A", "委员", type);
            y9OptionValueManager.create("002K", "常务委员", type);
            y9OptionValueManager.create("004A", "主任", type);
            y9OptionValueManager.create("004B", "副主任", type);
            y9OptionValueManager.create("010A", "巡视员", type);
            y9OptionValueManager.create("011A", "调研员", type);
            y9OptionValueManager.create("011B", "副调研员", type);
            y9OptionValueManager.create("015A", "纪检员", type);
            y9OptionValueManager.create("083Q", "总工程师", type);
            y9OptionValueManager.create("083R", "副总工程师", type);
            y9OptionValueManager.create("102A", "秘书长", type);
            y9OptionValueManager.create("102B", "副秘书长", type);
            y9OptionValueManager.create("105A", "检察长", type);
            y9OptionValueManager.create("105B", "副检察长", type);
            y9OptionValueManager.create("201A", "主席", type);
            y9OptionValueManager.create("201B", "副主席", type);
            y9OptionValueManager.create("211A", "部长", type);
            y9OptionValueManager.create("211B", "副部长", type);
            y9OptionValueManager.create("211J", "部长助理", type);
            y9OptionValueManager.create("212A", "审计长", type);
            y9OptionValueManager.create("212B", "副审计长", type);
            y9OptionValueManager.create("214A", "署长", type);
            y9OptionValueManager.create("214B", "副署长", type);
            y9OptionValueManager.create("215A", "关长", type);
            y9OptionValueManager.create("215B", "副关长", type);
            y9OptionValueManager.create("216A", "局长", type);
            y9OptionValueManager.create("216B", "副局长", type);
            y9OptionValueManager.create("219A", "处长", type);
            y9OptionValueManager.create("219B", "副处长", type);
            y9OptionValueManager.create("220A", "科长", type);
            y9OptionValueManager.create("220B", "副科长", type);
            y9OptionValueManager.create("221S", "主任科员", type);
            y9OptionValueManager.create("221T", "副主任科员", type);
            y9OptionValueManager.create("221A", "科员", type);
            y9OptionValueManager.create("224A", "办事员", type);
            y9OptionValueManager.create("251A", "省长", type);
            y9OptionValueManager.create("251B", "副省长", type);
            y9OptionValueManager.create("252A", "市长", type);
            y9OptionValueManager.create("252B", "副市长", type);
            y9OptionValueManager.create("252J", "市长助理", type);
            y9OptionValueManager.create("254A", "州长", type);
            y9OptionValueManager.create("254B", "副州长", type);
            y9OptionValueManager.create("255A", "区长", type);
            y9OptionValueManager.create("255B", "副区长", type);
            y9OptionValueManager.create("256A", "盟长", type);
            y9OptionValueManager.create("256B", "副盟长", type);
            y9OptionValueManager.create("257A", "专员", type);
            y9OptionValueManager.create("257B", "副专员", type);
            y9OptionValueManager.create("258A", "县长", type);
            y9OptionValueManager.create("258B", "副县长", type);
            y9OptionValueManager.create("259A", "旗长", type);
            y9OptionValueManager.create("259B", "副旗长", type);
            y9OptionValueManager.create("260A", "镇长", type);
            y9OptionValueManager.create("260B", "副镇长", type);
            y9OptionValueManager.create("261A", "乡长", type);
            y9OptionValueManager.create("261B", "副乡长", type);
            y9OptionValueManager.create("262A", "村长", type);
            y9OptionValueManager.create("262B", "副村长", type);
            y9OptionValueManager.create("410A", "会长", type);
            y9OptionValueManager.create("410B", "副会长", type);
            y9OptionValueManager.create("405A", "理事长", type);
            y9OptionValueManager.create("405B", "副理事长", type);
            y9OptionValueManager.create("416A", "所长", type);
            y9OptionValueManager.create("416B", "副所长", type);
            y9OptionValueManager.create("417A", "院长", type);
            y9OptionValueManager.create("417B", "副院长", type);
            y9OptionValueManager.create("418A", "校长", type);
            y9OptionValueManager.create("418B", "副校长", type);
            y9OptionValueManager.create("438A", "站长", type);
            y9OptionValueManager.create("438B", "副站长", type);
            y9OptionValueManager.create("801A", "参谋长", type);
            y9OptionValueManager.create("802A", "参谋", type);
            y9OptionValueManager.create("851A", "政委", type);
            y9OptionValueManager.create("840A", "大队长", type);
            y9OptionValueManager.create("840B", "副大队长", type);
            y9OptionValueManager.create("841A", "队长", type);
            y9OptionValueManager.create("841B", "副队长", type);
            y9OptionValueManager.create("842A", "负责人", type);
        } else if ("dutyLevel".equals(type)) {
            y9OptionValueManager.create("1", "国家主席、副主席、总理级", type);
            y9OptionValueManager.create("2", "副总理、国务委员级", type);
            y9OptionValueManager.create("3", "部、省级", type);
            y9OptionValueManager.create("5", "司、局、地厅级", type);
            y9OptionValueManager.create("6", "副司、副局、副地、副厅级", type);
            y9OptionValueManager.create("7", "县、处级", type);
            y9OptionValueManager.create("8", "副县、副处级", type);
            y9OptionValueManager.create("9", "正科级", type);
            y9OptionValueManager.create("10", "副科级", type);
            y9OptionValueManager.create("11", "科员级", type);
            y9OptionValueManager.create("12", "办事员级", type);
            y9OptionValueManager.create("14", "县长", type);
        } else if ("dutyType".equals(type)) {
            y9OptionValueManager.create("1", "领导", type);
            y9OptionValueManager.create("2", "公务员", type);
        } else if ("officialType".equals(type)) {
            y9OptionValueManager.create("1", "公务员", type);
            y9OptionValueManager.create("2", "行政编", type);
            y9OptionValueManager.create("3", "事业编", type);
            y9OptionValueManager.create("4", "企业编", type);
            y9OptionValueManager.create("5", "其他", type);
        } else if ("organizationType".equals(type)) {
            y9OptionValueManager.create("11", "公司", type);
            y9OptionValueManager.create("13", "非公司制企业法人", type);
            y9OptionValueManager.create("15", "企业分支机构", type);
            y9OptionValueManager.create("17", "个人独资企业、合伙企业", type);
            y9OptionValueManager.create("19", "其它企业", type);
            y9OptionValueManager.create("31", "中国共产党", type);
            y9OptionValueManager.create("32", "国家权力机关法人", type);
            y9OptionValueManager.create("33", "国家行政机关法人", type);
            y9OptionValueManager.create("34", "国家司法机关法人", type);
            y9OptionValueManager.create("35", "政协组织", type);
            y9OptionValueManager.create("36", "民主党派", type);
            y9OptionValueManager.create("37", "人民解放军、武警部队", type);
            y9OptionValueManager.create("39", "其他机关", type);
            y9OptionValueManager.create("51", "事业单位法人", type);
            y9OptionValueManager.create("53", "事业单位分支、派出机构", type);
            y9OptionValueManager.create("59", "其它事业单位", type);
            y9OptionValueManager.create("71", "社会团体法人", type);
            y9OptionValueManager.create("73", "社会团体分支、代表机构", type);
            y9OptionValueManager.create("79", "其它社会团体", type);
            y9OptionValueManager.create("91", "民办非企业单位", type);
            y9OptionValueManager.create("93", "基金会", type);
            y9OptionValueManager.create("94", "宗教活动场所", type);
            y9OptionValueManager.create("95", "农村居民委员会", type);
            y9OptionValueManager.create("96", "城市居民委员会", type);
            y9OptionValueManager.create("97", "自定义区", type);
            y9OptionValueManager.create("99", "其它", type);
        } else if ("principalIDType".equals(type)) {
            y9OptionValueManager.create("10", "身份证", type);
            y9OptionValueManager.create("11", "护照", type);
            y9OptionValueManager.create("12", "户口簿", type);
            y9OptionValueManager.create("13", "军人证", type);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteByType(String type) {
        y9OptionValueManager.deleteByType(type);
        y9OptionClassRepository.deleteById(type);
    }

    @Override
    @Transactional(readOnly = false)
    public void initOptionClass() {
        createOptionClass("职务", "duty");
        createOptionClass("职级", "dutyLevel");
        createOptionClass("编制类型", "officialType");
        createOptionClass("机构类型", "organizationType");
        createOptionClass("证件类型", "principalIDType");
    }

    @Override
    @Transactional(readOnly = true)
    public List<Y9OptionClass> list() {
        return y9OptionClassRepository.findAll();
    }

    @Override
    @Transactional(readOnly = false)
    public Y9OptionClass saveOptionClass(Y9OptionClass optionClass) {
        return y9OptionClassRepository.save(optionClass);
    }
}
