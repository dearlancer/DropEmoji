# DropEmoji
仿照微信表情掉落
使用贝塞尔曲线，可选择多图
可选择飘起或者下落模式
基础用法
```
  dropImg=new DropImg(MainActivity.this,R.drawable.lu000)
                .addImg(R.drawable.lu001)
                //.addImgList(list)
                .setRise(false)   //true为上升，false为下落
                .setImgNum(10);   //设置图片数量
        dropImg.show();
```        
