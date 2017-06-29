Id转换插件:
--
近俩月开始接触kotlin,在开发了两三个模块后,准备往项目里引入,项目是一个已经开发了三年的.几乎没有过重构的项目,代码量在十万行左右.引入的各种库很多.其中包括了一个我写的基于aspectJ写的注入框架,这时候kotlin的与其产生了冲突.有一定机率出现,注入查找不到控件的情况.引入kotlin为个人引入,与开发周期无关.这时候我需要顶着工期的压力去解决这个问题,于是便想到用插件,去网上找了一通.发现这块的插件有两个RemoveButterKnife/ButterKnifeKiller,但都不尽人意,后面去看了RemoveButterKnife的源码.发现其使用的方式是分析源码去操作.匹配失败率极高.所以才自己写了这个插件

* 插件功能
	* 支持单个类直接转换注入代码
	* 支持完级递归替换
	* 提供了良好的扩展机制(目前支持Activity/Fragment/Dialog/RecyclerView.ViewHolder),但拥有良好的扩展机制
	* 支持自定义注解配置,目前支持功能为
		* BindInit-初始化类
		* BindActivity-绑定类
   		* BindView-绑定字段控件
   		* BindString-绑定字符串资源
   		* BindColor-绑定颜色资源
		* BindArray-绑定数组
		* BindFieldBitmap-绑定bitmap
		* BindFieldDrawable-绑定drawable
		* BindFieldAnim-绑定anim
		* BindFieldDimen-绑定dimen
		* BindMethodClick-绑定方法单击
		* BindMethodLongClick-绑定方法长按
	* 完全基于idea open api,做到最精准的转换

</br>
* 功能演示</br>

*转换单个类*</br>
![gif1](https://raw.githubusercontent.com/momodae/ConvertInject/master/gif/2017062912321755666gif1.gif)

*批量转换*</br>
![gif2](https://raw.githubusercontent.com/momodae/ConvertInject/master/gif/2017062912315656357gif2.gif)

*转换模板配置*</br>
![gif3](https://raw.githubusercontent.com/momodae/ConvertInject/master/gif/2017062912310225161gif3.gif)
	
