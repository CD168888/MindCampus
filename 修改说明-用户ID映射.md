# 用户ID与学生ID映射关系修正

## 🔄 修改说明

根据您的反馈，我已经修正了用户身份识别的逻辑。原先直接使用 `SecurityUtils.getUserId()` 作为学生ID是不正确的，实际上应该：

1. **Controller层**: 获取当前登录用户的 `userId`（系统用户ID）
2. **Service层**: 通过 `userId` 查询 `student_info` 表，获取对应的 `studentId`（学生业务ID）
3. **业务操作**: 所有测评相关的数据库操作都使用 `studentId`

---

## 📊 数据库表关系

```
sys_user (系统用户表 - 若依框架)
├── user_id (PK)
└── username, password, etc.
         ↓ 一对一关联
student_info (学生信息表 - 业务表)
├── student_id (PK) ← 业务主键，用于测评数据关联
├── user_id (FK) ← 关联 sys_user.user_id
└── name, gender, grade, major, etc.
         ↓ 一对多关联
evaluation_result (测评结果表)
├── result_id (PK)
├── student_id (FK) ← 关联 student_info.student_id
└── questionnaire_id, total_score, etc.
```

---

## 🐛 Bug 修复

### 表名映射错误
**问题**: `Student` 实体类缺少 `@TableName` 注解，导致 MyBatis Plus 默认查询表名 `student`，但实际表名是 `student_info`。

**错误日志**:
```
Table 'ry-vue.student' doesn't exist
SQL: SELECT ... FROM student WHERE (user_id = ?)
```

**解决方案**: 在 `Student` 类上添加 `@TableName("student_info")` 注解

**修改的文件**: `mc-project/src/main/java/com/mc/student/domain/Student.java`

```java
@Data
@TableName("student_info")  // ← 新增
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "学生信息实体")
public class Student extends BaseEntity {
    // ...
}
```

---

## 🔧 修改的文件

### 1. Controller 层
**文件**: `mc-project/src/main/java/com/mc/evaluation/controller/AppAssessmentController.java`

**修改内容**:
```java
// 修改前：直接使用 getUserId() 作为 studentId
Long studentId = SecurityUtils.getUserId();

// 修改后：明确获取的是 userId，传递给 Service 层
Long userId = SecurityUtils.getUserId();
```

**影响的方法**:
- `getStatistics()` - 获取统计数据
- `listQuestionnaires()` - 查询问卷列表
- `submitAnswer()` - 提交答题
- `getResultDetail()` - 查询结果详情
- `listMyResults()` - 查询历史记录
- `checkCompleted()` - 检查完成状态

---

### 2. Service 接口
**文件**: `mc-project/src/main/java/com/mc/evaluation/service/IAppAssessmentService.java`

**修改内容**: 
```java
// 修改前：参数名为 studentId
AssessmentStatisticsVO getStatistics(Long studentId);

// 修改后：参数名改为 userId，语义更清晰
AssessmentStatisticsVO getStatistics(Long userId);
```

**所有方法的第一个参数都从 `studentId` 改为 `userId`**

---

### 3. Service 实现类
**文件**: `mc-project/src/main/java/com/mc/evaluation/service/impl/AppAssessmentServiceImpl.java`

**新增私有方法**:
```java
/**
 * 通过userId查询studentId
 */
private Long getStudentIdByUserId(Long userId) {
    LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(Student::getUserId, userId);
    Student student = studentInfoMapper.selectOne(wrapper);
    
    if (student == null) {
        throw new ServiceException("学生信息不存在，请先完善学生档案");
    }
    
    return student.getStudentId();
}
```

**所有方法的开头都添加**:
```java
@Override
public XxxVO someMethod(Long userId, ...) {
    // 通过userId查询studentId
    Long studentId = getStudentIdByUserId(userId);
    
    // 后续业务逻辑使用 studentId
    ...
}
```

**修改的方法**:
- `getStatistics()` ✅
- `listQuestionnaires()` ✅
- `submitAnswer()` ✅
- `getResultDetail()` ✅
- `listMyResults()` ✅
- `checkCompleted()` ✅

---

## ✨ 优化效果

### 1. 语义清晰
- **Controller**: 明确获取的是系统用户ID
- **Service**: 明确接收的是系统用户ID，内部转换为学生ID
- **业务逻辑**: 统一使用学生ID，避免混淆

### 2. 数据隔离
- 系统用户（sys_user）：负责登录认证
- 学生档案（student_info）：负责业务数据
- 测评数据（evaluation_result）：关联学生档案

### 3. 异常处理
如果学生未完善档案，会抛出友好的异常提示：
```
"学生信息不存在，请先完善学生档案"
```

### 4. 扩展性
这种设计支持：
- 一个系统用户对应一个学生档案
- 未来可扩展为多角色（学生、教师、咨询师）
- 不同角色使用不同的业务ID

---

## 🧪 测试建议

### 场景1：正常流程
1. 使用学生账号登录
2. 确保该用户在 `student_info` 表中有对应记录
3. 访问测评列表
4. 正常显示数据

### 场景2：异常处理
1. 使用系统用户登录（非学生）
2. 或学生未完善档案
3. 访问测评列表
4. 应返回异常提示："学生信息不存在，请先完善学生档案"

### 场景3：数据隔离
1. 用户A（userId=1）→ 学生档案A（studentId=101）
2. 用户B（userId=2）→ 学生档案B（studentId=102）
3. A只能看到studentId=101的测评数据
4. B只能看到studentId=102的测评数据

---

## 📝 更新的文档

1. ✅ `心理测评模块开发文档.md` - 添加"用户ID与学生ID映射"章节
2. ✅ `README-测评模块.md` - 更新核心功能说明和使用说明
3. ✅ `修改说明-用户ID映射.md` - 本文档（新增）

---

## ⚠️ 注意事项

1. **数据库前置条件**:
   - `student_info` 表必须存在
   - `student_info.user_id` 字段必须正确关联 `sys_user.user_id`

2. **学生档案完善**:
   - 学生首次登录后，应引导完善学生档案
   - 或管理员批量导入学生信息时，确保 `user_id` 关联正确

3. **前端无需修改**:
   - 前端代码无需修改
   - Token中的用户信息由后端自动处理

4. **权限控制**:
   - 当前接口未添加权限注解
   - 仅通过 `studentId` 进行数据隔离
   - 生产环境建议添加角色权限控制

---

## 🔍 代码审查要点

如果您需要审查代码，请关注以下几点：

1. ✅ Controller中是否明确使用 `userId` 变量名
2. ✅ Service接口参数是否改为 `userId`
3. ✅ Service实现中是否在方法开头调用 `getStudentIdByUserId()`
4. ✅ 后续业务逻辑是否使用转换后的 `studentId`
5. ✅ 异常处理是否友好

---

## 📞 相关联系

如有其他问题或需要进一步修改，请随时告知！

**修改时间**: 2025-11-04  
**修改人**: Cursor AI Assistant  
**影响范围**: APP端心理测评模块（4个文件）
  - AppAssessmentController.java
  - IAppAssessmentService.java
  - AppAssessmentServiceImpl.java
  - Student.java （Bug修复）

