package com.bootvuedemo.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bootvuedemo.common.util.RspResult;
import com.bootvuedemo.converter.UserExcelToPoConverter;
import com.bootvuedemo.dao.mapper.UserMapper;
import com.bootvuedemo.entity.User;
import com.bootvuedemo.service.UserService;
import com.bootvuedemo.util.WebUtil;
import com.bootvuedemo.util.mapAndEntityConvert.MapAndEntityConvertUtil;
import com.planetProvide.easyExcelPlus.core.baseDao.BaseDao;
import com.planetProvide.easyExcelPlus.core.baseExcelImportValid.BaseExcelImportValid;
import com.planetProvide.easyExcelPlus.core.baseReadListener.BaseRowReadListener;
import com.planetProvide.easyExcelPlus.core.entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Planet
 * @since 2022-02-24
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Value("${readBatchCount}") //100
    private long readBatchCount;
    @Autowired
    private BaseExcelImportValid<com.bootvuedemo.entity.excel.User> baseExcelImportValid;
    @Autowired
    private BaseDao<com.bootvuedemo.entity.excel.User> baseDao;
    @Autowired
    private UserExcelToPoConverter userExcelToPoConverter;

    @Transactional
    @Override
    public Integer inserts(List<User> list) {
        if(list==null&&list.size()<=0){
            throw new RuntimeException("新增失败:集合不能为空..事务回滚");
        }else if(list.size()==1){
            if(!save(list.get(0))){
                throw new RuntimeException("新增失败,事务回滚");
            }
        }else{
            if(!saveBatch(list)){
                throw new RuntimeException("新增失败,事务回滚");
            }
        }
        return list.size();
    }

    @Transactional
    @Override
    public RspResult excelImport(MultipartFile excelFile) {
        InputStream in=null;
        try {
            in = excelFile.getInputStream();
            BaseRowReadListener<com.bootvuedemo.entity.excel.User> baseRowReadListener=new BaseRowReadListener<>(
                    readBatchCount,baseExcelImportValid,baseDao,userExcelToPoConverter
            );
            Class<?>[] parameterTypes=new Class[1];
//            parameterTypes[0]=MultipartFile[].class;
            parameterTypes[0]= List.class;
            baseRowReadListener.setParameterTypes(parameterTypes);
            ExcelReaderBuilder workBook = EasyExcel.read(in, com.bootvuedemo.entity.excel.User.class, baseRowReadListener);
            // 封装工作表
            ExcelReaderSheetBuilder sheet1 = workBook.sheet();
            // 读取
            sheet1.doRead();

            //3.要拿出BaseReadListener对象中的resultCode和unqualifiedRows来获知校验和持久化的结果:这两个值可以直接返回给前端,让前端展示相应的效果给用户
            Result<com.bootvuedemo.entity.excel.User> result = baseRowReadListener.getResult();
            System.out.println(result);
            int resultCode = result.getResultCode();
            if(result.getResultCode()==0){//导入成功
                return RspResult.SUCCESS;
            }else{//将失败结果返回
                return new RspResult("用户记录导入失败","000001",result);
            }
        }catch (IOException e) {
            e.printStackTrace();
            log.error("用户记录导入失败");
            throw new RuntimeException("用户记录导入失败,事务回滚");
        }finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void excelExport(User user) {
        HttpServletResponse response = WebUtil.getResponse();
        // 1.模板
        InputStream templateInputStream = this.getClass().getClassLoader().getResourceAsStream(
                "templates/excel/用户信息模块-模板.xlsx");

        // 2.目标文件
        String targetFile = "用户信息模块-记录.xlsx";

        //3.模型实体类的类对象
        Class userClass= com.bootvuedemo.entity.excel.User.class;

        // 4.写入workbook对象
        ExcelWriter workBook =null;
        try {
            workBook = EasyExcel.write(response.getOutputStream(),userClass).withTemplate(templateInputStream).build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //5.准备工作表和对应的数据
        WriteSheet sheet = EasyExcel.writerSheet().build();

        //6.获取数据
        QueryWrapper<User> tQueryWrapper = new QueryWrapper<>();
        Map<String, Object> stringObjectMap = null;
        try {
            stringObjectMap = MapAndEntityConvertUtil.entityToMap(user, 0,true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return;
        }

        //将需要作为查询条件的字段装配到QueryWrapper上,用于拼装查询sql(目前我们只做等于这种,以后会改进成各种方式的)
        for (String key : stringObjectMap.keySet()) {
            if("size".equals(key)||"current".equals(key)){
                continue;
            }
            tQueryWrapper.eq(key,stringObjectMap.get(key));
        }
//            IPage<T> pageData = iService.page(page, tQueryWrapper.orderByAsc("id"));
        List<User> list=list(tQueryWrapper.orderByAsc("id"));
        List<com.bootvuedemo.entity.excel.User> roleInfos = userExcelToPoConverter.convertPoToExcel(list);

        //7. 写入数据

        workBook.fill(roleInfos, sheet);

        //8.构建响应对象
        response.setContentType("application/vnd.ms-excel");
//        response.setContentType("application/octet-stream;charset=ISO8859-1");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码
        String fileName = null;
        try {
            fileName = URLEncoder.encode(targetFile, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName);
        workBook.finish();
    }
}
