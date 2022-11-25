# Red Star Rule Engine

# I. Giới thiệu

Ý tưởng nảy ra khi cần một công cụ kiểm tra điều kiệu của hai object mà không cần hard code, thay vào đó chương trình sẽ tự biên dịch dựa theo script có sẵn và tiến hành chỉnh cấu hình lại dự trên object đầu vào

Ví dụ về file script

```java
define com.example.demodrools.testdit.ClassA classA;
define com.example.demodrools.testdit.ClassB classB;
define com.example.demodrools.testdit.ClassC classC;

rule "Calculate Risk - Scenario 1"
    when
        classA.method == "DEPOSIT";
        (classA.amount + classB.amount) < 100;
    then
        classC.status = "accept";
        classC.message = "accept transactional";
end

rule "Calculate Risk - Scenario 2"
    when
        classA.method == "DEPOSIT";
        (classA.amount + classB.amount) > 100;
    then
        classC.status = "accept";
        classC.message = "accept transactional";
end
```