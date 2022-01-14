package net.xinmu.pbbot.plugin;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import net.xinmu.pbbot.bean.WeiBo;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 多功能插件实现案例
 * api调用网站 ：https://api.iyk0.com/
 * hutool工具包：https://www.hutool.cn/
 */
@Component
public class DianGePlugin extends BotPlugin {
    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {
        String message = event.getRawMessage();
        long groupId = event.getGroupId();
        if (message.startsWith("点歌")&&message.length()>2){
            String miscName = message.substring(2);
            try {
                JSONObject jsonObject = requestApi(miscName.trim());
                String img = jsonObject.getStr("img");
                String song = jsonObject.getStr("song");
                String singer = jsonObject.getStr("singer");
                String url = jsonObject.getStr("url");
                Msg.builder().music(song,singer,url,img,url).sendToGroup(bot,groupId);
            }catch (Exception e){
                String format = String.format("搜索不到与[%s]的相关歌曲，请稍后重试或换个关键词试试。", miscName.trim());
                Msg.builder().text(format).sendToGroup(bot,groupId);
            }

        }

        if (message.startsWith("网易点歌")&&message.length()>4){
            String miscName = message.substring(4);
            try {
                JSONObject jsonObject = requestApi19(miscName.trim());
                String img = jsonObject.getStr("img");
                String song = jsonObject.getStr("song");
                String singer = jsonObject.getStr("singer");
                String url = jsonObject.getStr("url");
                Msg.builder().music(song,singer,url,img,url).sendToGroup(bot,groupId);
            }catch (Exception e){
                String format = String.format("搜索不到与[%s]的相关歌曲，请稍后重试或换个关键词试试。", miscName.trim());
                Msg.builder().text(format).sendToGroup(bot,groupId);
            }

        }

        if (message.startsWith("ping")&&message.length()>4){
            String urlName = message.substring(4);
            try {
                String res = requestApi14(urlName.trim());
                Msg.builder().text(res).sendToGroup(bot,groupId);
            }catch (Exception e){
                String format = String.format("[%s]可能不符合格式要求！", urlName.trim());
                Msg.builder().text(format).sendToGroup(bot,groupId);
            }
        }

        if (message.startsWith("备案查询")&&message.length()>4){
            String urlName = message.substring(4);
            try {
                String res = requestApi15(urlName.trim());
                Msg.builder().text(res).sendToGroup(bot,groupId);
            }catch (Exception e){
                String format = String.format("[%s]可能不符合格式要求！", urlName.trim());
                Msg.builder().text(format).sendToGroup(bot,groupId);
            }
        }

        if (message.startsWith("在线查询")&&message.length()>4){
            String urlName = message.substring(4);
            try {
                String res = requestApi16(urlName.trim());
                String img = String.format("https://api.iyk0.com/qqimg?qq=%s",urlName.trim());
                Msg.builder().text("qq:").text(urlName).image(img).text(res).sendToGroup(bot,groupId);
            }catch (Exception e){
                String format = String.format("[%s]可能不符合格式要求！", urlName.trim());
                Msg.builder().text(format).sendToGroup(bot,groupId);
            }
        }


        if (message.startsWith("天气")&&message.length()>2){
            String urlName = message.substring(2);
            try {
                String res = requestApi17(urlName.trim());
                Msg.builder().json(0,res).sendToGroup(bot,groupId);
            }catch (Exception e){
                String format = String.format("[%s]可能不符合格式要求！", urlName.trim());
                Msg.builder().text(format).sendToGroup(bot,groupId);
            }
        }


        if (message.startsWith("歌词")&&message.length()>2) {
            String miscDict = message.substring(2);
            JSONObject jsonObject = requestApi2(miscDict.trim(),1);
            String code = jsonObject.getStr("code");
            if ("200".equals(code)){
                String Keyword = jsonObject.getStr("Keyword");
                String data = jsonObject.getStr("data");
                Msg.builder().text("歌曲名：").text(Keyword).text("\r\n歌词: \r\n").text(data).sendToGroup(bot,groupId);
            }else {
                Msg.builder().text("请求失败！").sendToGroup(bot,groupId);
            }

        }

        if ("百度热搜".equals(message)) {
            JSONObject jsonObject = requestApi3();
            String code = jsonObject.getStr("code");
            if ("200".equals(code)){
//                String Keyword = jsonObject.getStr("Keyword");
                JSONArray data = jsonObject.getJSONArray("data");
                Msg text = Msg.builder().text("百度热搜榜Top10\r\n");
                for (int i = 0; i < data.size(); i++) {
                    String title = JSONUtil.parseObj(data.get(i)).getStr("title");
                    text.text(String.valueOf(i+1)).text(".").text(title).text("\r\n");
                    if (i==9) {
                        break;
                    }
                }
                text.sendToGroup(bot,groupId);
            }else {
                Msg.builder().text("请求失败！").sendToGroup(bot,groupId);
            }

        }

        if (message.startsWith("星座运势")&&message.length()>4) {
            String msg = message.substring(4);
            JSONObject jsonObject = requestApi10(msg.trim());
            String code = jsonObject.getStr("code");
            if ("200".equals(code)){
                String data = jsonObject.getStr("data");
                Msg.builder().text(data).sendToGroup(bot,groupId);
            }else {
                Msg.builder().text("请求失败！").sendToGroup(bot,groupId);
            }

        }

        if ("微博热搜".equals(message)) {
            List<WeiBo> weiBos = requestApi4();
            Msg text = Msg.builder().text("微博热搜榜Top10\r\n");
            for (int i = 0; i < weiBos.size(); i++) {
                text.text(String.valueOf(i+1)).text(".").text(weiBos.get(i).getTitle()).text("\r\n");
                if (i==9) {
                    break;
                }
            }
            text.sendToGroup(bot,groupId);
        }

        if ("骚话".equals(message)) {
            Msg.builder().text(requestApi5()).sendToGroup(bot,groupId);
        }
        if ("精神语录".equals(message)) {
            Msg.builder().text(requestApi6()).sendToGroup(bot,groupId);
        }
        if ("毒鸡汤".equals(message)) {
            Msg.builder().text(requestApi7()).sendToGroup(bot,groupId);
        }
        if ("舔狗日记".equals(message)) {
            Msg.builder().text(requestApi8()).sendToGroup(bot,groupId);
        }
        if ("渣男语录".equals(message)) {
            Msg.builder().text(requestApi9()).sendToGroup(bot,groupId);
        }

        if ("伤感语录".equals(message)) {
            Msg.builder().text(requestApi11()).sendToGroup(bot,groupId);
        }

        if ("彩虹屁".equals(message)) {
            JSONObject jsonObject = requestApi12();
            String code = jsonObject.getStr("code");
            if ("200".equals(code)){
                String data = jsonObject.getStr("txt");
                Msg.builder().text(data).sendToGroup(bot,groupId);
            }else {
                Msg.builder().text("请求失败！").sendToGroup(bot,groupId);
            }
        }

        if ("笑话".equals(message)) {
            Msg.builder().text(requestApi18()).sendToGroup(bot,groupId);
        }

        if (message.startsWith("搜视频")&&message.length()>3){
            String msg = message.substring(3);
            JSONObject jsonObject = requestApi13(msg.trim());
            String code = jsonObject.getStr("code");
            if ("200".equals(code)){
                String img = jsonObject.getStr("img");
                String url = jsonObject.getStr("url");
                String type = jsonObject.getStr("type");
                Msg.builder().video(url,img,true).sendToGroup(bot,groupId);
            }else {
                Msg.builder().text("请求失败！").sendToGroup(bot,groupId);
            }
        }


        if (message.startsWith("mm")&&message.length()>2){
            String msg = message.substring(2);
            Msg text = Msg.builder();
            try {
                int i = Integer.parseInt(msg);
                if (i>5){
                    Msg.builder().text("数量过多！上限5张图！！").sendToGroup(bot,groupId);
                    return MESSAGE_IGNORE;
                }
                for (int j = 0; j < i; j++) {
                    text.image("https://api.iyk0.com/mtyh/");
                }
                text.sendToGroup(bot,groupId);
            }catch (Exception e){
                Msg.builder().text("指令有误!请输入mm图片数量如：mm3").sendToGroup(bot,groupId);
            }

        }

        if (message.startsWith("mn")&&message.length()>2){
            String msg = message.substring(2);
            Msg text = Msg.builder();
            try {
                int i = Integer.parseInt(msg);
                if (i>5){
                    Msg.builder().text("数量过多！上限5张图！！").sendToGroup(bot,groupId);
                    return MESSAGE_IGNORE;
                }
                for (int j = 0; j < i; j++) {
                    text.image("https://api.iyk0.com/mn/2");
                }
                text.sendToGroup(bot,groupId);
            }catch (Exception e){
                Msg.builder().text("指令有误!请输入mn图片数量如：mn3").sendToGroup(bot,groupId);
            }

        }

        if (message.startsWith("cos")&&message.length()>3){
            String msg = message.substring(3);
            Msg text = Msg.builder();
            try {
                int i = Integer.parseInt(msg);
                if (i>5){
                    Msg.builder().text("数量过多！上限5张图！").sendToGroup(bot,groupId);
                    return MESSAGE_IGNORE;
                }
                for (int j = 0; j < i; j++) {
                    text.image("https://api.iyk0.com/cos/");
                }
                text.sendToGroup(bot,groupId);
            }catch (Exception e){
                Msg.builder().text("指令有误!请输入cos图片数量如：cos3").sendToGroup(bot,groupId);
            }

        }

        if (".功能".equals(message)||".帮助".equals(message)||".菜单".equals(message)||".指令".equals(message)||".插件".equals(message)){
            Msg text = Msg.builder();
            text.text("1、").text("mm*|mn* *代表输入数字 美图").text("\r\n");
            text.text("2、").text("cos* *代表输入数字 cosplay图").text("\r\n");
            text.text("3、").text("点歌|网易点歌 歌曲名").text("\r\n");
            text.text("4、").text("歌词 任意歌词").text("\r\n");
            text.text("5、").text("百度热搜").text("\r\n");
            text.text("6、").text("微博热搜").text("\r\n");
            text.text("7、").text("精神语录|伤感语录|舔狗日记|彩虹屁|毒鸡汤|渣男语录|笑话").text("\r\n");
            text.text("8、").text("搜视频 可选参数说明：网红、明星、热舞、风景、游戏、动物").text("\r\n");
            text.text("9、").text("星座运势 星座").text("\r\n");
            text.text("10、").text("ping 域名").text("\r\n");
            text.text("11、").text("备案查询 域名").text("\r\n");
            text.text("12、").text("在线查询 QQ号");
            text.sendToGroup(bot,groupId);
        }

        return super.onGroupMessage(bot, event);
    }
    // 随机笑话
    // 调用hutool工具包请求
    public String requestApi18(){
        String apiUrl="https://api.iyk0.com/xh/";
        String res = HttpUtil.get(apiUrl);
        return res;
    }

    /**
     * 天气在线查询
     * @param trim
     * @return
     */
    private String requestApi17(String trim) {
        String apiUrl="https://api.iyk0.com/tq/?city=%s&type=json";
        String format = String.format(apiUrl, trim);
        String res = HttpUtil.get(format);
        return res;
    }

    /**
     * QQ电脑在线查询
     * @param trim
     * @return
     */
    private String requestApi16(String trim) {
//        https://api.iyk0.com/qqzx/?qq=2822569653
        String apiUrl="https://api.iyk0.com/qqzx/?qq=%s";
        String format = String.format(apiUrl, trim);
        String res = HttpUtil.get(format);
        return JSONUtil.parseObj(res).getStr("msg");
    }

    /**
     * 网易云音乐查询
     * @param trim
     * @return
     */
    private JSONObject requestApi19(String trim) {
        String apiUrl="https://api.iyk0.com/wymusic/?msg=%s&n=1";
        String format = String.format(apiUrl, trim);
        String res = HttpUtil.get(format);
        return JSONUtil.parseObj(res);
    }

    /**
     * 备案查询
     * @param trim
     * @return
     */
    private String requestApi15(String trim) {
        String apiUrl="https://api.iyk0.com/beian/?url=%s";
        String format = String.format(apiUrl, trim);
        String res = HttpUtil.get(format);
        return res;
    }

    /**
     * 在线ping
     * @param trim
     * @return
     */
    private String requestApi14(String trim) {
        String apiUrl="https://api.iyk0.com/ping/?url=%s";
        String format = String.format(apiUrl, trim);
        String res = HttpUtil.get(format);
        return res;
    }

    // 调用hutool工具包请求
    //https://api.iyk0.com/gcsg/?Keyword=乘坐地铁三号线
    private JSONObject requestApi2(String trim, int i) {
        String apiUrl="https://api.iyk0.com/gcsg/?Keyword=%s&n=1";
        String api = String.format(apiUrl,trim);
        String res = HttpUtil.get(api);
        JSONObject jsonObject = JSONUtil.parseObj(res);
        return jsonObject;
    }

    // 调用hutool工具包请求
    //https://api.iyk0.com/gcsg/?Keyword=乘坐地铁三号线
    public JSONObject requestApi(String miscName){
        String apiUrl="https://api.iyk0.com/qqmusic/?msg=%s&n=1";
        String api = String.format(apiUrl, miscName);
        String res = HttpUtil.get(api);
        JSONObject jsonObject = JSONUtil.parseObj(res);
        return jsonObject;
    }

    // 调用hutool工具包请求
    //百度热搜
    public JSONObject requestApi3(){
        String apiUrl="https://api.iyk0.com/bdr/";
        String res = HttpUtil.get(apiUrl);
        JSONObject jsonObject = JSONUtil.parseObj(res);
        return jsonObject;
    }

    // 调用hutool工具包请求
    //微博热搜
    public List<WeiBo>  requestApi4(){
        String apiUrl="https://api.iyk0.com/wbr/";
        String res = HttpUtil.get(apiUrl);
        List<WeiBo> weiBos = JSON.parseArray("["+res+"]", WeiBo.class);
        return weiBos;
    }
    // 随机骚话
    // 调用hutool工具包请求
    public String requestApi5(){
        String apiUrl="https://api.iyk0.com/sao/";
        String res = HttpUtil.get(apiUrl);

        return res;
    }
    // 精神语录
    // 调用hutool工具包请求
    public String requestApi6(){
        String apiUrl="https://api.iyk0.com/jsyl/";
        String res = HttpUtil.get(apiUrl);

        return res;
    }

    // 随机毒鸡汤
    // 调用hutool工具包请求
    public String requestApi7(){
        String apiUrl="https://api.iyk0.com/du/";
        String res = HttpUtil.get(apiUrl);
        String data = JSONUtil.parseObj(res).getStr("data");
        return data;
    }

    // 舔狗日记
    // 调用hutool工具包请求
    public String requestApi8(){
        String apiUrl="https://api.iyk0.com/tiangou/";
        String res = HttpUtil.get(apiUrl);
        return res;
    }

    // 渣男语录
    // 调用hutool工具包请求
    public String requestApi9(){
        String apiUrl="https://api.iyk0.com/zhanan/";
        String res = HttpUtil.get(apiUrl);
        return res;
    }

    // 调用hutool工具包请求
    //星座运势
    public JSONObject requestApi10(String msg){
        String apiUrl="https://api.iyk0.com/xzys/?msg=%s";
        String api = String.format(apiUrl, msg);
        String res = HttpUtil.get(api);
        JSONObject jsonObject = JSONUtil.parseObj(res);
        return jsonObject;
    }

    // 调用hutool工具包请求
    // 伤感语录
    public String requestApi11(){
        String apiUrl="https://api.iyk0.com/sg/";
        String res = HttpUtil.get(apiUrl);
        return res;
    }


    //请求调用hutool工具包请求
    public JSONObject requestApi12(){
        String apiUrl="https://api.iyk0.com/chp/";
        String res = HttpUtil.get(apiUrl);
        return JSONUtil.parseObj(res);
    }

    //请求调用hutool工具包请求
    //精选短视频类型 可选参数说明：网红、明星、热舞、风景、游戏、动物
    public JSONObject requestApi13(String msg){
        String apiUrl="https://api.iyk0.com/dsp/?type=%s";
        String api = String.format(apiUrl, msg);
        String res = HttpUtil.get(api);
        return JSONUtil.parseObj(res);
    }



    public String toXml(JSONObject jsonObject){
        String img = jsonObject.getStr("img");
        String song = jsonObject.getStr("song");
        String singer = jsonObject.getStr("singer");
        String url = jsonObject.getStr("url");
        String xml="{\"app\":\"com.tencent.structmsg\",\"config\":{\"autosize\":true,\"ctime\":%d,\"forward\":true,\"token\":\"52399f3861cc5e735ca84c178583fab4\",\"type\":\"normal\"},\"desc\":\"音乐\",\"extra\":{\"app_type\":1,\"appid\":100495085,\"msg_seq\":7051763387857781476,\"uin\":2733363076},\"meta\":{\"music\":{\"action\":\"\",\"android_pkg_name\":\"\",\"app_type\":1,\"appid\":100495085,\"ctime\":1641866609,\"desc\":\"%s\",\"jumpUrl\":\"%s\",\"musicUrl\":\"%s\",\"preview\":\"%s\",\"sourceMsgId\":\"0\",\"source_icon\":\"\",\"source_url\":\"\",\"tag\":\"网易云音乐\",\"title\":\"%s\",\"uin\":2733363076}},\"prompt\":\"[分享]%s\",\"ver\":\"0.0.0.1\",\"view\":\"music\"}";
        String format = String.format(xml,System.currentTimeMillis()/1000,singer,url, url,img, song, song);
        System.out.println(format);
        return format;
    }

}
