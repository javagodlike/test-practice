package mock.zjunit;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.ignoreStubs;
// 静态导入会使代码更简洁
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ StaticMethod.class })
public class BasicTest {

	/**
	 * 验证某些行为
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testVerify() {
		// mock creation 创建mock对象
		List mockedList = mock(List.class);

		// using mock object 使用mock对象
		mockedList.add("one");
		mockedList.clear();

		// verification 验证
		verify(mockedList).add("one");
		verify(mockedList).clear();
	}

	/**
	 * 如何做一些测试桩 (Stub)
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testStub() {
		// You can mock concrete classes, not only interfaces
		// 你可以mock具体的类型,不仅只是接口
		LinkedList mockedList = mock(LinkedList.class);

		// stubbing
		// 测试桩
		when(mockedList.get(0)).thenReturn("first");
		when(mockedList.get(1)).thenThrow(new RuntimeException());

		// following prints "first"
		// 输出“first”
		System.out.println(mockedList.get(0));

		// following throws runtime exception
		// 抛出异常
		System.out.println(mockedList.get(1));

		// following prints "null" because get(999) was not stubbed
		// 因为get(999) 没有打桩，因此输出null
		System.out.println(mockedList.get(999));

		// Although it is possible to verify a stubbed invocation, usually it's just
		// redundant
		// If your code cares what get(0) returns then something else breaks (often
		// before even verify() gets executed).
		// If your code doesn't care what get(0) returns then it should not be stubbed.
		// Not convinced? See here.
		// 验证get(0)被调用的次数
		verify(mockedList).get(0);
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testArgs() {
		// mock creation 创建mock对象
		List mockedList = mock(List.class);
		// stubbing using built-in anyInt() argument matcher
		// 使用内置的anyInt()参数匹配器
		when(mockedList.get(anyInt())).thenReturn("element");

		// stubbing using custom matcher (let's say isValid() returns your own matcher
		// implementation):
		// 使用自定义的参数匹配器( 在isValid()函数中返回你自己的匹配器实现 )
		when(mockedList.contains(argThat(isValid()))).thenReturn(true);
		System.out.println(mockedList.contains("asdf"));

		// following prints "element"
		// 输出element
		System.out.println(mockedList.get(999));

		// you can also verify using an argument matcher
		// 你也可以验证参数匹配器
		verify(mockedList).get(anyInt());
	}

	private ArgumentMatcher<Object> isValid() {
		return new ArgumentMatcher() {

			public boolean matches(Object argument) {
				return "asdf".equals(argument);
			}
		};
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testTimes() {
		// mock creation 创建mock对象
		List mockedList = mock(List.class);
		// using mock
		mockedList.add("once");

		mockedList.add("twice");
		mockedList.add("twice");

		mockedList.add("three times");
		mockedList.add("three times");
		mockedList.add("three times");

		mockedList.add("five times");
		mockedList.add("five times");

		// following two verifications work exactly the same - times(1) is used by
		// default
		// 下面的两个验证函数效果一样,因为verify默认验证的就是times(1)
		verify(mockedList).add("once");
		verify(mockedList, times(1)).add("once");

		// exact number of invocations verification
		// 验证具体的执行次数
		verify(mockedList, times(2)).add("twice");
		verify(mockedList, times(3)).add("three times");

		// verification using never(). never() is an alias to times(0)
		// 使用never()进行验证,never相当于times(0)
		verify(mockedList, never()).add("never happened");

		// verification using atLeast()/atMost()
		// 使用atLeast()/atMost()
		verify(mockedList, atLeastOnce()).add("three times");
		verify(mockedList, atLeast(2)).add("five times");
		verify(mockedList, atMost(5)).add("three times");
	}

	@SuppressWarnings("rawtypes")
	@Test(expected = RuntimeException.class)
	public void expectedException() {
		// mock creation 创建mock对象
		List mockedList = mock(List.class);
		doThrow(new RuntimeException()).when(mockedList).clear();

		// following throws RuntimeException:
		// 调用这句代码会抛出异常
		mockedList.clear();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test()
	public void testRedundant() {
		// mock creation 创建mock对象
		List mockedList = mock(List.class);
		// using mocks
		mockedList.add("one");
		mockedList.add("two");

		verify(mockedList).add("one");

		// following verification will fail
		verifyNoMoreInteractions(mockedList);
	}

	@Test
	public void testArgumentCaptor() {
		Person p = new Person();
		p.setName("John");

		ArgumentCaptor<Person> argument = ArgumentCaptor.forClass(Person.class);
		Teacher mock = mock(Teacher.class);
		mock.doSomething(p);
		verify(mock).doSomething(argument.capture());
		assertEquals("John", argument.getValue().getName());
	}

	@Test
	public void spySimpledemo() {
		List<String> list = new LinkedList<String>();
		List<String> spy = Mockito.spy(list);
		when(spy.size()).thenReturn(100);

		spy.add("one");
		spy.add("two");

		/*
		 * spy的原理是，如果不打桩默认都会执行真实的方法，如果打桩则返回桩实现。
		 * 可以看出spy.size()通过桩实现返回了值100，而spy.get(0)则返回了实际值
		 */
		assertEquals(spy.get(0), "one");
		assertEquals(100, spy.size());
	}

	@Test
	public void spyProcessionDemo() {
		Jack spyJack = spy(new Jack());
		// 使用spy的桩实现实际还是会调用stub的方法，只是返回了stub的值
		when(spyJack.go()).thenReturn(false);
		assertFalse(spyJack.go());

		// 不会调用stub的方法
		doReturn(false).when(spyJack).go();
		assertFalse(spyJack.go());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void reset() {
		List mock = mock(List.class);
		when(mock.size()).thenReturn(10);
		mock.add(1);
		System.out.println(mock.size());
		// reset之后，mock被初始化了，之前的stub没有了
		Mockito.reset(mock);
		// at this point the mock forgot any interactions & stubbing
		System.out.println(mock.size());
	}

	Seller seller = mock(Seller.class);
	Shop shop = new Shop(seller);

	@SuppressWarnings("rawtypes")
	@Test
	public void testIgnore() {
		// mocking lists for the sake of the example (if you mock List in real you will
		// burn in hell)
		List mock1 = mock(List.class), mock2 = mock(List.class);

		// stubbing mocks:
		when(mock1.get(0)).thenReturn(10);
		when(mock2.get(0)).thenReturn(20);

		// using mocks by calling stubbed get(0) methods:
		System.out.println(mock1.get(0)); // prints 10
		System.out.println(mock2.get(0)); // prints 20

		// using mocks by calling clear() methods:
		mock1.clear();
		mock2.clear();

		// verification:
		verify(mock1).clear();
		verify(mock2).clear();

		// verifyNoMoreInteractions() fails because get() methods were not accounted
		// for.
		verifyNoMoreInteractions(mock1, mock2);

		// However, if we ignore stubbed methods then we can verifyNoMoreInteractions()
		verifyNoMoreInteractions(ignoreStubs(mock1, mock2));

		// Remember that ignoreStubs() *changes* the input mocks and returns them for
		// convenience.
	}

	@Test
	public void testStatic() {
		StaticMethod s = spy(new StaticMethod());
		// when(s.queryDb()).thenReturn(null);
		doReturn(null).when(s).queryDb();
		s.test();
	}

	@Test
	public void testStatic2() {
		PowerMockito.mockStatic(StaticMethod.class);
		when(StaticMethod.checkPerson(ArgumentMatchers.anyString())).thenReturn(false);
		boolean checkPerson = StaticMethod.checkPerson("");
		PowerMockito.verifyStatic(StaticMethod.class);
		assertFalse(checkPerson);
	}

}
