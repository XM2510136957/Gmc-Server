package net.xinmu.pbbot.plugin;

import cn.hutool.http.HttpUtil;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * 点歌插件实现案例
 * api调用网站 ：https://api.iyk0.com/
 * hutool工具包：https://www.hutool.cn/
 */
@Component
public class LiaoTianPlugin extends BotPlugin {
    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {
        String message = event.getRawMessage();
        long groupId = event.getGroupId();
        long selfId = bot.getSelfId();
        String format = String.format("<at qq=\"%d\"/>", selfId);
        if(message.startsWith(format)){
            try {
                String substring = message.substring(format.length() + 1);
                Msg.builder().text(requestApi(substring)).sendToGroup(bot,groupId);
            }catch (Exception e){
                Msg.builder().text(requestApi("???")).sendToGroup(bot,groupId);
            }

        }
        return super.onGroupMessage(bot, event);
    }

    @Override
    public int onPrivateMessage(@NotNull Bot bot, @NotNull OnebotEvent.PrivateMessageEvent event) {

        String message = event.getRawMessage();
        if(message.startsWith(".")){
            String substring = message.substring(1);
            Msg.builder().text(requestApi(substring)).sendToFriend(bot,event.getUserId());
        }

        return super.onPrivateMessage(bot, event);
    }

    //调用hutool工具包请求聊天接口
    private String requestApi(String message) {
        String apiUrl="https://api.iyk0.com/liaotian/?msg=%s";
        String api = String.format(apiUrl,message);
        String res = HttpUtil.get(api);
        return res;
    }

}
