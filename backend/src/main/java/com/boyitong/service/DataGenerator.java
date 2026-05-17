package com.boyitong.service;

import com.boyitong.entity.*;
import com.boyitong.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.IntStream;

@Component
@Order(2)
public class DataGenerator implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataGenerator.class);
    private final CustomerRepository customerRepo;
    private final ProductRepository productRepo;
    private final OpportunityRepository oppRepo;
    private final ContractRepository contractRepo;
    private final PaymentRepository paymentRepo;
    private final AnnouncementRepository announcementRepo;
    private final UserRepository userRepository;
    private final Random r = new Random(42);
    private final String[] systemUsers = {"zhangrui", "wangxian"};
    private final String[] salesPeople = {"张睿", "王鲜", "改飞", "李华", "赵明", "陈静", "刘洋", "周梅", "吴强", "郑丽"};
    private Map<String, Long> userIdMap = Map.of();

    public DataGenerator(CustomerRepository customerRepo, ProductRepository productRepo,
                         OpportunityRepository oppRepo, ContractRepository contractRepo,
                         PaymentRepository paymentRepo, AnnouncementRepository announcementRepo,
                         UserRepository userRepository) {
        this.customerRepo = customerRepo;
        this.productRepo = productRepo;
        this.oppRepo = oppRepo;
        this.contractRepo = contractRepo;
        this.paymentRepo = paymentRepo;
        this.announcementRepo = announcementRepo;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        long customerCount = customerRepo.count();
        // Build userId map for setting userId fields
        Map<String, Long> map = new HashMap<>();
        userRepository.findAll().forEach(u -> map.put(u.getUsername(), u.getId()));
        this.userIdMap = map;
        if (customerCount > 1500) {
            log.info("Database already has {} customers, skipping customer generation", customerCount);
        } else {
            generateCustomers();
        }

        if (productRepo.count() == 0) generateProducts();
        if (oppRepo.count() == 0) generateOpportunities();
        if (contractRepo.count() == 0) generateContracts();
        if (announcementRepo.count() == 0) generateAnnouncements();

        log.info("Data generation complete. Customers: {}, Products: {}, Opportunities: {}, Contracts: {}, Announcements: {}",
                customerRepo.count(), productRepo.count(), oppRepo.count(), contractRepo.count(), announcementRepo.count());
    }

    private void generateCustomers() {
        log.info("Generating customer data...");
        List<Customer> newData = new ArrayList<>();
        newData.addAll(generateCity("郑州", 120));
        newData.addAll(generateCity("西安", 100));
        newData.addAll(generateCity("成都", 140));
        newData.addAll(generateCity("武汉", 110));
        newData.addAll(generateCity("长沙", 90));
        newData.addAll(generateCity("广州", 130));
        newData.addAll(generateCity("深圳", 110));
        newData.addAll(generateCity("杭州", 95));
        newData.addAll(generateCity("南京", 85));
        newData.addAll(generateCity("重庆", 120));
        newData.addAll(generateCity("柳州", 80));
        newData.addAll(generateCity("鄂尔多斯", 100));
        customerRepo.saveAll(newData);
        log.info("Generated {} customers", newData.size());
    }

    private void generateProducts() {
        log.info("Generating products...");
        String[][] productData = {
            {"商铺信息发布服务", "信息服务", "次", "299.00"},
            {"VIP商铺推广套餐", "推广服务", "月", "1999.00"},
            {"金牌铺位推荐位", "推广服务", "周", "599.00"},
            {"客户线索匹配服务", "信息服务", "次", "99.00"},
            {"店铺评估报告", "咨询服务", "份", "499.00"},
            {"转让流程代办", "代办服务", "次", "1499.00"},
            {"合同拟定服务", "咨询服务", "份", "399.00"},
            {"实地考察服务", "咨询服务", "次", "899.00"},
            {"线上推广置顶", "推广服务", "天", "199.00"},
            {"商铺摄影服务", "配套服务", "套", "299.00"},
            {"小程序店铺展示", "配套服务", "月", "499.00"},
            {"精准客源推送", "信息服务", "月", "799.00"},
            {"品牌加盟推荐", "信息服务", "次", "199.00"},
            {"法律咨询顾问", "咨询服务", "时", "599.00"},
            {"营业执照代办", "代办服务", "次", "899.00"},
        };
        for (String[] p : productData) {
            Product product = new Product();
            product.setName(p[0]); product.setCategory(p[1]);
            product.setUnit(p[2]); product.setPrice(Double.parseDouble(p[3]));
            productRepo.save(product);
        }
        log.info("Generated {} products", productData.length);
    }

    private void generateOpportunities() {
        log.info("Generating opportunities...");
        List<Customer> customers = customerRepo.findAll();
        if (customers.isEmpty()) return;
        String[] stages = {"INTENT", "PROPOSAL", "QUOTATION", "NEGOTIATION", "WON", "LOST"};
        double[] winRates = {10, 30, 50, 70, 100, 0};

        for (int i = 0; i < 50; i++) {
            Customer c = customers.get(r.nextInt(customers.size()));
            String stage = stages[r.nextInt(stages.length)];
            Opportunity o = new Opportunity();
            o.setName(c.getCategory() + " - " + c.getCity() + c.getArea());
            o.setCustomerId(c.getId());
            o.setAmount(5000 + r.nextDouble() * 95000);
            o.setStage(stage);
            o.setWinRate(winRates[Arrays.asList(stages).indexOf(stage)]);
            o.setSalesperson(r.nextBoolean() ? "admin" : "zhangrui");
            o.setSalespersonUserId(userIdMap.get(o.getSalesperson()));
            o.setDescription("跟进" + c.getCity() + "客户关于" + c.getCategory() + "的商机");
            oppRepo.save(o);
        }
        log.info("Generated 50 opportunities");
    }

    private void generateContracts() {
        log.info("Generating contracts...");
        List<Customer> customers = customerRepo.findAll();
        if (customers.isEmpty()) return;
        String[] statuses = {"DRAFT", "ACTIVE", "COMPLETED", "TERMINATED"};

        for (int i = 0; i < 30; i++) {
            Customer c = customers.get(r.nextInt(customers.size()));
            Contract ct = new Contract();
            ct.setContractNo("CT-" + (100000 + i));
            ct.setName(c.getCategory() + "服务合同 - " + c.getCity());
            ct.setCustomerId(c.getId());
            ct.setAmount(10000 + r.nextDouble() * 90000);
            String status = statuses[r.nextInt(statuses.length)];
            ct.setStatus(status);
            ct.setStartDate(LocalDate.of(2026, 1 + r.nextInt(5), 1 + r.nextInt(28)));
            ct.setEndDate(ct.getStartDate().plusMonths(1 + r.nextInt(12)));
            ct.setSalesperson("admin");
            ct.setSalespersonUserId(userIdMap.get("admin"));
            ct.setDescription(c.getAddress());
            contractRepo.save(ct);

            // Generate some payments
            if ("ACTIVE".equals(status) || "COMPLETED".equals(status)) {
                int payments = 1 + r.nextInt(3);
                for (int p = 0; p < payments; p++) {
                    Payment pm = new Payment();
                    pm.setContractId(ct.getId());
                    pm.setAmount(ct.getAmount() / payments);
                    pm.setPlanDate(ct.getStartDate().plusMonths(p));
                    pm.setStatus(r.nextBoolean() ? "PAID" : "PENDING");
                    pm.setRemark("第" + (p + 1) + "期回款");
                    paymentRepo.save(pm);
                }
            }
        }
        log.info("Generated 30 contracts with payments");
    }

    private void generateAnnouncements() {
        log.info("Generating announcements...");
        String[][] announcements = {
            {"平台服务费调整通知", "尊敬的客户：自2026年6月1日起，平台将对VIP推广套餐价格进行调整，新价格将在原价基础上上浮10%。感谢您的理解与支持。", "admin", "true"},
            {"柳州地区业务拓展公告", "公司已成功拓展柳州鱼峰区新业务线，欢迎有店铺转让需求的客户联系我们。新区域首批合作客户可享受8折优惠。", "admin", "true"},
            {"五一放假安排", "五一劳动节期间（5月1日-5月3日），公司安排值班人员，紧急事务请联系值班经理：张睿 138****9999。", "admin", "false"},
            {"系统升级通知", "博易通CRM系统已完成升级，新增产品管理、商机管理、合同管理等模块，欢迎各位同事使用并提出宝贵意见。", "admin", "false"},
            {"优秀销售评选结果", "4月份优秀销售评选结果：冠军张睿（成交12单），亚军王鲜（成交9单），季军改飞（成交7单）。恭喜三位！", "admin", "false"},
            {"新员工入职通知", "欢迎李华、赵明、陈静三位新同事加入销售团队！请各位老同事多多指导，帮助他们尽快熟悉业务流程。", "admin", "false"},
            {"月度销售会议通知", "兹定于5月15日下午2点在会议室召开月度销售总结会议，请全体销售人员准时参加。会议将总结4月业绩并部署5月目标。", "admin", "false"},
        };
        for (String[] a : announcements) {
            Announcement ann = new Announcement();
            ann.setTitle(a[0]); ann.setContent(a[1]);
            ann.setAuthor(a[2]); ann.setPinned("true".equals(a[3]));
            announcementRepo.save(ann);
        }
        log.info("Generated {} announcements", announcements.length);
    }

    // ======== Customer generation helpers ========
    private List<Customer> generateCity(String city, int count) {
        List<Customer> list = new ArrayList<>();
        String[] areas = getAreas(city);
        String[] categories = {"商铺转让", "餐饮转让", "店面转让", "店铺转让", "超市转让",
                "美容转让", "酒吧转让", "火锅店转让", "烧烤店转让", "奶茶店转让",
                "服装店转让", "棋牌室转让", "快递驿站转让", "宾馆转让", "培训机构转让"};

        for (int i = 0; i < count; i++) {
            Customer c = new Customer();
            c.setCity(city);
            c.setDate(String.format("%d.%02d", r.nextInt(12) + 1, r.nextInt(28) + 1));
            c.setArea(areas[r.nextInt(areas.length)]);
            c.setCategory(categories[r.nextInt(categories.length)]);
            int sizeBase = 20 + r.nextInt(500);
            c.setAddress(generateDescription(c.getCategory(), sizeBase));
            c.setSize((double) sizeBase);
            c.setPhone("1" + (r.nextInt(3) + 3) + String.format("%09d", r.nextInt(1000000000)));
            c.setExpiryDate(r.nextBoolean() ? String.format("%d.%02d", 2026 + r.nextInt(2), r.nextInt(12) + 1) : "");
            c.setSalesperson(salesPeople[r.nextInt(salesPeople.length)]);
            c.setRemarks(r.nextInt(10) > 7 ? "价格可谈" : "");
            c.setStatus(r.nextDouble() < 0.3 ? "NEW" : r.nextDouble() < 0.5 ? "FOLLOWING" : r.nextDouble() < 0.7 ? "NEGOTIATING" : r.nextDouble() < 0.85 ? "WON" : "LOST");
            String sysUser = systemUsers[r.nextInt(systemUsers.length)];
            c.setAssignedTo(sysUser);
            c.setAssignedToUserId(userIdMap.get(sysUser));
            list.add(c);
        }
        return list;
    }

    private String generateDescription(String category, int size) {
        String[] prefixes = {"急转", "低价急转", "整体转让", "营业中", "精装", "新装修"};
        String[] suffixes = {"设备全", "客源稳定", "接手可营业", "地段好", "人流大", "租金优", "停车方便"};
        String prefix = prefixes[r.nextInt(prefixes.length)];
        String suffix1 = suffixes[r.nextInt(suffixes.length)];
        String suffix2 = suffixes[r.nextInt(suffixes.length)];
        return prefix + " " + size + "m²" + category.replace("转让", "").replace("转", "") + " " + suffix1 + " " + suffix2;
    }

    private String[] getAreas(String city) {
        switch (city) {
            case "柳州": return new String[]{"柳北区","柳南区","城中区","鱼峰区","柳江区","柳城","融安","鹿寨"};
            case "鄂尔多斯": return new String[]{"东胜区","康巴什区","伊旗","达旗","鄂拖旗","准格尔旗","杭锦旗"};
            case "郑州": return new String[]{"金水区","二七区","中原区","管城区","惠济区","郑东新区","高新区"};
            case "西安": return new String[]{"雁塔区","碑林区","莲湖区","新城区","未央区","长安区","高新区"};
            case "成都": return new String[]{"锦江区","青羊区","金牛区","武侯区","成华区","高新区","天府新区"};
            case "武汉": return new String[]{"武昌区","江汉区","硚口区","汉阳区","洪山区","江岸区","东湖高新"};
            case "长沙": return new String[]{"岳麓区","芙蓉区","天心区","开福区","雨花区","望城区"};
            case "广州": return new String[]{"天河区","越秀区","海珠区","荔湾区","白云区","番禺区","黄埔区"};
            case "深圳": return new String[]{"南山区","福田区","罗湖区","宝安区","龙岗区","龙华区","光明区"};
            case "杭州": return new String[]{"西湖区","上城区","拱墅区","滨江区","余杭区","萧山区"};
            case "南京": return new String[]{"鼓楼区","玄武区","秦淮区","建邺区","江宁区","浦口区"};
            case "重庆": return new String[]{"渝中区","江北区","沙坪坝区","九龙坡区","南岸区","渝北区"};
            default: return new String[]{"中心区","东区","西区","南区","北区"};
        }
    }
}