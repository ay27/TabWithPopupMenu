---
layout: post
title: 带筛选选项的Tab控件
date: 2014-12-19 21:40:30 +0800
comments: true
categories: 
---

##描述
最近无意中看到，一个app的ActionBar，在Tab中可以弹出下拉菜单，感觉是一个不错的设计。无奈搜索一圈，没有发现任何相关的库实现，那就自己写一个吧^_^

<!--more-->

先看我实现出来的效果：
<img src="/images/post/tab1.png" alt="">

##思路
首先，` 不能 `使用ActionBar的提供的Tab，因为它根本不能实现这样的弹出菜单，也很难监听点击操作。那么，只能自己模仿了。

先简单的将Tab划成两个部分，一个是上面的按钮，一个是底下的指示条。
<img src="/images/post/tab2.png" alt="">

底下的指示条好处理，直接根据ViewPager的滚动信息动态描绘出来即可。

问题是，我们该选用什么控件来实现那几个按钮？

由于有下拉菜单的存在，很容易想到的是用一个spinner，加几个Button来组装。问题是，spinner不好定制啊，而且由于spinner现实的是下拉菜单中选中的一项啊。这可不是我们想要的，我们想要的是：spinner在没有弹出菜单时，显示的内容跟旁边几个button是一类的，弹出菜单后，无论怎么选择都不会影响那个显示的内容。最后，得出以下结论：

1. 按钮组使用 ` RadioGroup` 实现，放弃 ` spinner `
2. 弹出菜单自己描绘一个，或者考虑调用系统的某些控件
3. 底下的指示条直接根据 ` ViewPager ` 的滚动信息动态描绘出来

##界面

####RadioGroup
RadioGroup中的每一个RadioButton中，界面的关键代码是下面这个：

{% highlight java %}

android:layout_width="fill_parent"
android:layout_weight="1"
android:button="@null"

{% endhighlight %}

* width和weight的设置，使得几个button放在一行上能平均分配位置，
* button设置为空是为了去除RadioButton那个丑陋的选中圈圈
* 至于怎么整出那个第一个按钮右下角的下拉角标，那很简单啦，画个图上去╮(╯▽╰)╭


####TabIndicator
一个自建的View，根据ViewPager传入的位置信息，动态地画出一根线。关键代码如下：
{% highlight java %}
private int pageNum;

@Override
protected void onDraw(Canvas canvas) {
	super.onDraw(canvas);
	canvas.drawLine(start, 0, start+length, 0, paint);
}

private int start;
private int length;

public void draw(int page, int start) {
	this.start = (page)*length+start/pageNum;
	this.invalidate();
}

public void setPageNum(int pageNum) {
	this.pageNum = pageNum;
	length = getResources().getDisplayMetrics().widthPixels / pageNum;
}
{% endhighlight %}

每次ViewPager滚动时，将当前页和当前滚动的位置传入到draw函数，即可自动描绘一条线。

在ViewPager的OnPageChangeListener中，设置这样的调用：
{% highlight java %}
@Override
public void onPageScrolled(int i, float v, int i1) {
	indicator.draw(i, i1);
}
{% endhighlight %}

####PopupMenu
那个弹出菜单是用了系统提供的PopupMenu，原本想着自己写的，无意中发现这么个空间╮(╯▽╰)╭。监听RadioButton1的点击操作，弹出一个PopupMenu即可。

{% highlight java %}
final PopupMenu popupMenu = new PopupMenu(this, button1);
popupMenu.getMenuInflater().inflate(R.menu.main, popupMenu.getMenu());
popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
	@Override
	public boolean onMenuItemClick(MenuItem item) {
		Toast.makeText(MyActivity.this, "click on " + item.getTitle(), Toast.LENGTH_SHORT).show();
		return true;
	}
});
{% endhighlight %}

##控件间的逻辑
首先，我们需要监听ViewPager的滚动信息，以便投递到Indicator和RadioGroup。

但是监听ViewPager的滚动信息比较麻烦，因为回调函数中有几个参数，必须清楚几个参数分别指示什么，不然想要做出效果还真有点麻烦。这里简单说明一下：
{% highlight java %}
// var1=current page, var2=scroll velocity, var3=scroll start position
void onPageScrolled(int var1, float var2, int var3);

// var1=selected page
void onPageSelected(int var1);

//var1=scroll state, var1==1:point down, var1==2:fling, var1==0:stop
void onPageScrollStateChanged(int var1);
{% endhighlight %}
需要注意的是，onPageSelected是在滚动超过一定界限时会触发，而不是滚动停下之后才触发，也就是说，onPageSelected触发后，还会触发一系列的onPageScrolled后才会触发onPageScrollStateChanged以指示滚动结束。

在滚动的途中，我们需要时刻投递滚动的位置和当前页信息到Indicator，当滚动结束后，需要把选中的页面投递到RadioGroup，以便同步显示。具体看我代码。

还有个问题，当我们点击RadioGroup中的某个RadioButton时，ViewPager需要跳转到目标页面。一句代码即可实现：
{% highlight java %}
pager.setCurrentItem(targetPage);
{% endhighlight %}

问题是，这个调用会触发onPageScrolled和onPageScrollStateChanged，这就引发一个问题：我们在onPageScrollStateChanged中改变了RadioGroup的选中状态，而RadioButton的点击会触发onPageScrollStateChanged。。。。

还好，由于调用setCurrentItem而导致的onPageScrollStateChanged，不会存在state==1的情况，也即没有手指按下的信息。这样我们就能分离开到底是我们手指的移动，还是RadioButton的点击，触发的onPageScrollStateChanged。

##写在最后
当前给出的是我自己实现的一个demo，其实很容易实现一个通用的库，以供使用。可是，授人以鱼不如授人以渔，我还是花点时间写下我的实现过程，让读者自行实现为好。也希望看到我的文章的人，也像我一样，宁愿花点时间写出自己的实现过程，也不要单单给出一个通用库那么单调^_^