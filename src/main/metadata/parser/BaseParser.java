/******************************************************************************
 * Copyright (C) 2013 - 2016 ShenZhen oneplus Technology Co.,Ltd
 * All Rights Reserved.
 * 本软件为深圳万普拉斯科技有限公司开发研制。未经本公司正式书面同意，其他任何个人、团体不得使用、复制、修改或发布本软件 .
 *****************************************************************************/
package main.metadata.parser;

import main.metadata.metadata.LianJiaHouse;
import org.jsoup.nodes.Document;

import java.util.List;

public interface BaseParser {

    boolean canDuplicate();

    List<LianJiaHouse> getHouseList(Document doc);
}
