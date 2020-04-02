# 原型模式

原型模式我在工作中用到的比较多的，有 `JSONUtil.parseObj(jsUserFans);` 、`BeanUtil.copyProperties(jsUserFans, jsFansVO)` 等，也就是进行大量属性的拷贝、VO 和 PO 转换等工作。

原型模式（Prototype Pattern）是指原型实例指定创建对象的种类，并且通过拷贝这些原型创建新的对象。

- 调用者不需要知道任何创建细节，不调用构造函数
  - 封装拷贝创建的细节
  - 克隆已经存在的对象
  - 对象构造比较复杂，通过克隆可以提升性能
  - 一般可基于二进制流进行复制，而不是简单的 set 、get
- 属于创建型模式

工作中原型模式的例子：http://note.youdao.com/noteshare?id=874a166aae53bf42e803c77073886e36

在写 Java 、Golang 、JS 中，我都写过 clone 的方法，本质上还是 get 、set 。

原型模式适用场景：

- 类初始化消耗资源较多
- new 产生的一个对象需要非常繁琐的过程（数据准备、访问权限等）
- 构造函数比较复杂
- 循环体中生产大量对象时

JDK 提供了 Cloneable 接口

- 实现了 Cloneable 接口的类表明它可以合法访问 java.lang.Object#clone() 
- java.lang.Object#clone() 完成 class 实例的字段到字段的拷贝
- 这个拷贝是 native 的底层的基于内存二进制流的拷贝
- 并且是浅拷贝，对于对象，只会拷贝引用
- 一个对象实例没有实现 Cloneable 接口
- 一个没有实现 Cloneable 接口的对象实例调用 Object 对象的 clone 方法会导致抛出 CloneNotSupportedException
- 实现 Cloneable 接口的类应该 override 重写 Object.clone 方法（本身是 protected）为 public 方法
- Cloneable 接口并不包含 clone 方法，仅仅实现此接口就调用 clone 方法并不一定保证能够成功，即使是通过反射调用

深克隆

- 可以通过序列化反序列化做到，与之前单例模式被序列化反序列化相对。

- 也可以使用 JSON 的方式
- 或者 BeanUtil 、BeanUtils 的方式
- 见工作中原型模式的例子：http://note.youdao.com/noteshare?id=874a166aae53bf42e803c77073886e36

这种深克隆方式的缺点问题？

- 性能问题，过程涉及 IO 、反射，涉及 IO 流要及时关闭
- 克隆可能破坏单例：因为不用通过构造方法就可以创建新的对象
  - 单例对象不应该实现 Cloneable 接口，不应该作为原型对象
  - 原型模式和单例模式是冲突的

框架源码里的原型模式：

- ```java
  public class ArrayList<E> extends AbstractList<E>
          implements List<E>, RandomAccess, Cloneable, java.io.Serializable {
      ……
      /**
       * Returns a shallow copy of this <tt>ArrayList</tt> instance.  (The
       * elements themselves are not copied.)
       *
       * @return a clone of this <tt>ArrayList</tt> instance
       */
      public Object clone() {
          try {
              ArrayList<?> v = (ArrayList<?>) super.clone();
              v.elementData = Arrays.copyOf(elementData, size);
              v.modCount = 0;
              return v;
          } catch (CloneNotSupportedException e) {
              // this shouldn't happen, since we are Cloneable
              throw new InternalError(e);
          }
      }
      ……
  }
  ```

- 如果 ArrayList 中元素引用的对象，那么这个实现 Cloneable 的拷贝，虽然其中使用 Arrays.copyOf 进行了一层的深拷贝，但是还是会存在浅拷贝的情况，深拷贝还是要自己实现

- 所以，深拷贝还是使用 Apache 、Spring 、JSON 、序列化等方式，比较通用方便

原型模式的优点

- 性能优良，Java 自带的原型模式是基于内存二进制流的拷贝，比直接 new 一个对象性能上提升了许多
- 可以使用深克隆方式保存对象的状态，使用原型模式将对象复制一份并将其状态保存起来，简化了创建过程

原型模式的缺点

- 必须配备克隆（或者可拷贝）的方法
- 当对已有类进行改造的时候，需要修改代码，违反了开闭原则
- 深拷贝、浅拷贝需要运用得当