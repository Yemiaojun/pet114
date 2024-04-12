package com.example.wechat.service;

import com.example.wechat.exception.DefaultException;
import com.example.wechat.model.*;
import com.example.wechat.repository.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private CategoryRepository categoryRepository;


    @Autowired
    private QuestionRecordRepository questionRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private ExamRecordRepository examRecordRepository;

    public Question addQuestion(Question question) {
        question.setVisible(true);
        return questionRepository.save(question);
    }

    public Optional<Question> updateQuestion(Question question) {
        Optional<Question> existingQuestion = questionRepository.findById(question.getId());
        if(existingQuestion.isPresent()) {
            // 这里可以添加其他的业务逻辑，例如检查问题内容的唯一性等
            // 更新问题信息
            return Optional.of(questionRepository.save(question));
        } else {
            return Optional.empty(); // 如果问题不存在，返回空Optional
        }
    }

    public Optional<Question> hideQuestion(String questionId) {
        Optional<Question> questionOptional = questionRepository.findById(new ObjectId(questionId));
        if(questionOptional.isPresent()) {
            Question question = questionOptional.get();
            question.setVisible(false); // 将问题设置为不可见
            questionRepository.save(question);
            return Optional.of(question);
        }
        return Optional.empty(); // 如果问题不存在，返回空Optional
    }


    public List<Question> findAllQuestions() {
        return questionRepository.findAll();
    }

    public List<Question> findAllVisibleQuestions() {
        return questionRepository.findAllVisibleQuestions();
    }


    public List<Question> findQuestionsByCategoryId(ObjectId categoryId) {
        return questionRepository.findByCategoryId(categoryId);
    }

    public List<Question> findQuestionsByCategoryName(String categoryName) {
        Optional<Category> categoryOpt = categoryRepository.findByName(categoryName);
        if (categoryOpt.isPresent()) {
            Category category = categoryOpt.get();
            return questionRepository.findByCategoryId(category.getId());
        }
        return List.of(); // 如果没有找到对应的Category，返回空列表
    }

    public Optional<Question> findQuestionById(String questionId) {
        return questionRepository.findById(new ObjectId(questionId));
    }

    public List<Question> findQuestionsByStemLike(String stem) {
        String regex = ".*" + stem + ".*";
        return questionRepository.findByStemLike(regex);
    }

    public List<Question> findVisibleQuestionsByCategoryId(ObjectId categoryId) {
        return questionRepository.findByCategoryIdAndVisible(categoryId);
    }

    public List<Question> findVisibleQuestionsByCategoryName(String categoryName) {
        Optional<Category> categoryOpt = categoryRepository.findByName(categoryName);
        if (categoryOpt.isPresent()) {
            Category category = categoryOpt.get();
            return questionRepository.findByCategoryIdAndVisible(category.getId());
        }
        return List.of(); // 如果没有找到对应的Category，返回空列表
    }

    public List<Question> findVisibleQuestionsByStemLike(String stem) {
        String regex = ".*" + stem + ".*";
        return questionRepository.findByStemLikeAndVisible(regex);
    }

    public List<Question> getRandomQuestions(Integer n, String categoryIdStr) {
        List<Question> questions;
        if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
            ObjectId categoryId = new ObjectId(categoryIdStr);
            questions = findVisibleQuestionsByCategoryId(categoryId);
        } else {
            questions = findAllVisibleQuestions();
        }
        if(n<=0){
            throw new DefaultException("非法的参数");
        }
        // 打乱问题列表并取前n个
        Collections.shuffle(questions);
        return questions.stream().limit(n).collect(Collectors.toList());
    }


    public void checkQuestionAnswers(List<String> answerList, List<Question> questionList, String examId, String userId) {
        Optional<User> userOpt = userRepository.findById(new ObjectId(userId));
        Optional<Exam> examOpt = examRepository.findById(new ObjectId(examId));

        if (!userOpt.isPresent() || !examOpt.isPresent()) {
            // 这里可以抛出一个自定义的异常或者处理用户和考试不存在的情况
            throw new DefaultException("考试不存在");
        }

        User user = userOpt.get();
        Exam exam = examOpt.get();

        // 尝试找到现有的ExamRecord

        Optional<ExamRecord> existingRecordOpt = examRecordRepository.findByUserAndExam(user.getId(), exam.getId());

        ExamRecord examRecord;
        if (existingRecordOpt.isPresent()) {
            examRecord = existingRecordOpt.get();
        } else {
            examRecord = new ExamRecord();
            examRecord.setExam(exam);
            examRecord.setUser(user);
            examRecord.setScore(0); // 初始化得分为0
        }

        double totalScoreEarned = 0;
        int totalExamScore = exam.getScore();

        for (int i = 0; i < questionList.size(); i++) {
            Question question = questionList.get(i);
            String providedAnswer = answerList.get(i);

            QuestionRecord record = new QuestionRecord();
            record.setQuestion(question);
            record.setChoice(providedAnswer);
            record.setTorF(question.getAnswer().equals(providedAnswer));
            record.setUser(user); // 使用查询得到的User实例
            record.setExam(exam); // 使用查询得到的Exam实例
            record.setTime(new Date());
            questionRecordRepository.save(record);

            // 更新得分
            if (record.getTorF()) {
                double questionScoreRatio = (double)question.getScore() / totalExamScore;
                double scoreForThisQuestion = questionScoreRatio * exam.getScore();
                totalScoreEarned += scoreForThisQuestion;
            }
        }

        // 更新或设置ExamRecord的得分
        examRecord.setScore((int)Math.round(totalScoreEarned));
        examRecord.setStatus("已完成");
        examRecordRepository.save(examRecord);
    }

    public void importQuestions() {
        String rawData = """
待定

题干: 狗狗每年至少应进行心脏驱虫多少次？
选项: ["1次", "2次", "3次", "4次"]
答案: B
分值: 5

题干: 宠物狗狗过热时的首选降温方式是？
选项: ["用冰水浸泡", "开启空调降温", "提供冰块舔食", "用湿毛巾覆盖"]
答案: D
分值: 5

题干: 宠物狗狗的正常心跳频率是多少？
选项: ["60-100次/分钟", "80-120次/分钟", "100-140次/分钟", "120-160次/分钟"]
答案: C
分值: 5

题干: 为了预防口腔疾病，狗狗的牙齿应该多久清洁一次？
选项: ["每天", "每周", "每月", "每年"]
答案: A
分值: 5

题干: 小猫的断奶期通常是在多大时候？
选项: ["2-4周龄", "4-6周龄", "6-8周龄", "8-10周龄"]
答案: C
分值: 5

题干: 狗狗的体温过高，超过了多少度算是发烧？
选项: ["38°C", "38.5°C", "39°C", "39.5°C"]
答案: D
分值: 5

题干: 宠物狗应接种哪种疫苗来预防狂犬病？
选项: ["狂犬病疫苗", "细小病毒疫苗", "冠状病毒疫苗", "钩端螺旋体疫苗"]
答案: A
分值: 5



内科

题干: 狗狗患有慢性肾病时，以下哪项饮食调整最为适宜？
选项: ["高蛋白饮食", "低磷饮食", "高碳水化合物饮食", "高钠饮食"]
答案: B
分值: 5

题干: 犬慢性肝病的早期症状不包括以下哪项？
选项: ["黄疸", "多饮多尿", "活力下降", "体重急剧增加"]
答案: D
分值: 5

题干: 猫咪出现呕吐现象，以下哪种情况最不需要紧急就医？
选项: ["连续呕吐超过一天", "呕吐物中带有血丝", "偶尔呕吐但行为正常", "呕吐后拒绝饮水和进食"]
答案: C
分值: 5

题干: 狗狗患上糖尿病，饲养者应该如何调整其饮食？
选项: ["增加高脂肪食物的摄入", "限制蛋白质摄入", "提供高纤维饮食", "增加碳水化合物的比例"]
答案: C
分值: 10

题干: 在猫咪中，哪种疾病是由猫冠状病毒引起的？
选项: ["猫瘟", "猫白血病", "猫传染性腹膜炎", "猫流感"]
答案: C
分值: 5

题干: 犬类最常见的心脏疾病是什么？
选项: ["二尖瓣疾病", "心肌病", "心脏虫病", "冠状动脉病"]
答案: A
分值: 10

题干: 对于患有甲状腺功能亢进的猫咪，以下哪种治疗方法不适用？
选项: ["放射性碘治疗", "手术切除甲状腺", "使用β-阻滞剂", "提供含碘量高的食物"]
答案: D
分值: 5

题干: 慢性肾病在犬只中的管理不包括下列哪项？
选项: ["提供高盐饮食", "限制蛋白质摄入", "保持充足的水分", "使用药物控制血压"]
答案: A
分值: 5

题干: 猫咪患有哮喘，下列哪种环境改变对其最为有利？
选项: ["提供干燥的环境", "保持环境湿度", "使用空气清新剂", "减少家中尘螨和其他过敏源"]
答案: D
分值: 5

题干: 犬患上癫痫时，以下哪种情况最需要立即就医？
选项: ["首次发作", "发作持续时间超过5分钟", "发作间期清醒", "发作频率较前减少"]
答案: B
分值: 5

题干: 对于犬患者，急性胰腺炎的典型治疗不包括以下哪项？
选项: ["禁食以减轻胰腺负担", "大量使用抗生素", "静脉输液以支持水电解质平衡", "适量给予止吐药"]
答案: B
分值: 5



传染病

题干: 犬瘟热的主要传播途径是什么？
选项: ["空气传播", "直接接触", "通过跳蚤", "食物和水"]
答案: A
分值: 5

题干: 费氏立克体病主要通过哪种方式传播？
选项: ["叮咬", "食物", "空气", "直接接触"]
答案: A
分值: 5

题干: 费氏立克体病主要通过哪种方式传播？
选项: ["叮咬", "食物", "空气", "直接接触"]
答案: A
分值: 5

题干: 猫白血病病毒(FeLV)主要通过什么方式传播？
选项: ["唾液", "尿液", "粪便", "皮肤接触"]
答案: A
分值: 5

题干: 狗狗感染了细小病毒后，以下哪个症状最早出现？
选项: ["呕吐", "发热", "食欲不振", "腹泻"]
答案: B
分值: 5

题干: 猫咪感染了猫瘟病毒后，治疗的首要目标是什么？
选项: ["抗病毒治疗", "补充体液和电解质", "止吐", "降温"]
答案: B
分值: 5

题干: 犬冠状病毒主要影响狗狗的哪个系统？
选项: ["呼吸系统", "消化系统", "神经系统", "泌尿系统"]
答案: B
分值: 5

题干: 哪种疾病是通过蚊子叮咬传播给狗狗的？
选项: ["心丝虫病", "狂犬病", "犬瘟热", "细小病毒感染"]
答案: A
分值: 5

题干: 猫艾滋病(FIV)的传播途径主要是什么？
选项: ["食物和水", "猫咪打架时的咬伤", "跳蚤叮咬", "空气中的飞沫"]
答案: B
分值: 5

题干: 狗狗狂犬病的预防最有效的方法是什么？
选项: ["口服药物", "年度疫苗接种", "避免与野生动物接触", "定期进行血液检查"]
答案: B
分值: 5

题干: 猫冠状病毒变异后可导致哪种严重疾病？
选项: ["猫传染性腹膜炎(FIP)", "猫瘟", "猫白血病", "猫艾滋病(FIV)"]
答案: A
分值: 5

题干: 犬细小病毒感染后，狗狗的哪个器官受损最严重？
选项: ["肝脏", "心脏", "肠道", "肾脏"]
答案: C
分值: 5

题干: 关于猫白血病，以下哪种说法是错误的？
选项: ["所有年龄的猫都有感染的风险", "仅通过直接接触病猫的粪便可以传播", "疫苗接种可以预防", "病毒可以通过唾液传播"]
答案: B
分值: 5



寄生虫病

题干: 猫咪最常见的寄生虫是？
选项: ["绦虫", "钩虫", "圆虫", "蚤"]
答案: C
分值: 5

题干: 为预防心丝虫病，狗狗应该如何处理？
选项: ["每月服用预防药物", "每年接种疫苗", "避免外出", "定期洗澡"]
答案: A
分值: 5

题干: 狗狗最常见的外寄生虫是什么？
选项: ["跳蚤", "绦虫", "蜱虫", "耳螨"]
答案: A
分值: 5

题干: 猫咪患有耳螨时，以下哪种症状最为典型？
选项: ["频繁摇头和抓耳", "过度舔毛", "缺乏食欲", "呕吐"]
答案: A
分值: 5

题干: 狗狗心丝虫病的预防措施是什么？
选项: ["定期洗澡", "避免与感染狗狗接触", "服用预防药物", "定期疫苗接种"]
答案: C
分值: 5

题干: 猫咪感染了哪种寄生虫后会出现“米粒症”症状？
选项: ["钩虫", "绦虫", "圆虫", "蜱虫"]
答案: B
分值: 5

题干: 犬类钩虫感染的治疗首选是什么？
选项: ["外用药物", "手术", "口服驱虫药", "注射疫苗"]
答案: C
分值: 5

题干: 如何最有效地预防猫咪和狗狗被跳蚤叮咬？
选项: ["仅在夏季使用跳蚤预防药物", "定期清洁宠物的生活环境", "给宠物穿上防蚤衣物", "全年使用跳蚤预防药物"]
答案: D
分值: 5

题干: 宠物猫被蜱虫叮咬后的正确处理方式是什么？
选项: ["直接用手拔除蜱虫", "使用酒精涂抹蜱虫后拔除", "使用专用蜱虫钳或圈慢慢旋转拔出", "用火烧蜱虫使其自行脱落"]
答案: C
分值: 5

题干: 犬类感染圆虫最常见的传播途径是什么？
选项: ["饮用受污染的水", "食用受感染的肉", "接触受污染的土壤", "空气传播"]
答案: C
分值: 5

题干: 对于预防猫咪感染心丝虫病，以下哪项措施是错误的？
选项: ["给宠物使用心丝虫预防药物", "限制猫咪夜间外出", "定期给宠物洗澡", "避免在蚊虫活跃季节长时间外出"]
答案: C
分值: 5

题干: 猫咪外寄生虫的预防措施不包括以下哪项？
选项: ["给猫咪穿防虫衣服", "使用跳蚤和蜱虫预防药物", "保持猫咪的活动区域干净", "定期更换猫咪的饮水"]
答案: A
分值: 5



外产科疾病
题干: 在猫咪中，最常见的生产障碍是？
选项: ["宫颈不全性开放", "子宫炎", "难产", "早产"]
答案: C
分值: 5

题干: 对于初产的宠物狗，最常见的分娩时间是？
选项: ["白天", "夜间", "凌晨", "傍晚"]
答案: B
分值: 5



常用手术
题干: 绝育手术在减少哪种疾病的风险中最为有效？
选项: ["心脏病", "癌症", "糖尿病", "关节炎"]
答案: B
分值: 5
                """;

        List<String> parts = Arrays.asList(rawData.trim().split("\n\n\n\n"));
        Category currentCategory = null;
        for (String part : parts) {
            // 处理类别
            String[] categoryAndQuestions = part.split("\n\n", 2);
            if (categoryAndQuestions.length > 0) {
                String categoryName = categoryAndQuestions[0];
                currentCategory = findOrCreateCategory(categoryName.trim());
            }
            if (categoryAndQuestions.length < 2) continue; // 没有题目跳过
            List<String> questions = Arrays.asList(categoryAndQuestions[1].split("\n\n"));
            for (String questionData : questions) {
                processQuestionData(questionData, currentCategory);
            }
        }
    }

    private void processQuestionData(String questionData, Category category) {
        String[] lines = questionData.split("\n");
        Question question = new Question();
        question.setStem(lines[0].substring(lines[0].indexOf(": ") + 2));
        question.setOptionList(Arrays.asList(lines[1].substring(lines[1].indexOf(": ") + 2).replaceAll("\\[|\\]", "").split(", ")));
        question.setAnswer(lines[2].substring(lines[2].indexOf(": ") + 2));
        question.setScore(Integer.parseInt(lines[3].substring(lines[3].indexOf(": ") + 2)));
        question.setCategory(category);
        question.setVisible(true);

        questionRepository.save(question);
    }

    private Category findOrCreateCategory(String categoryName) {
        return categoryRepository.findByName(categoryName)
                .orElseGet(() -> {
                    Category newCategory = new Category();
                    newCategory.setName(categoryName);
                    return categoryRepository.save(newCategory);
                });
    }



}
