package com.min.smalltalk.message.module;

import java.util.List;

import io.rong.imkit.DefaultExtensionModule;
import io.rong.imkit.RongExtension;
import io.rong.imkit.emoticon.IEmoticonTab;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imlib.model.Conversation;

/**
 * Created by Min on 2016/12/21.
 */

public class TalkExtensionModule extends DefaultExtensionModule {
    private MyStartRecognizePlugin myStartRecognizePlugin;
//    private MyEndRecognizePlugin myEndRecognizePlugin;
    private LocationPlugin locationPlugin;

    @Override
    public void onAttachedToExtension(RongExtension extension) {
//        recognize = new RecognizePlugin();
        myStartRecognizePlugin=new MyStartRecognizePlugin();
//        myEndRecognizePlugin=new MyEndRecognizePlugin();
        locationPlugin = new LocationPlugin();
//        recognize.init(extension.getContext());
        myStartRecognizePlugin.init(extension.getContext());
//        myEndRecognizePlugin.init(extension.getContext());
        locationPlugin.init(extension.getContext());
        super.onAttachedToExtension(extension);
    }

    @Override
    public void onDetachedFromExtension() {
        super.onDetachedFromExtension();
    }

    @Override
    public List<IPluginModule> getPluginModules(Conversation.ConversationType conversationType) {
        List<IPluginModule> pluginModules =  super.getPluginModules(conversationType);
//        pluginModules.add(recognize);
        pluginModules.add(myStartRecognizePlugin);
//        pluginModules.add(myEndRecognizePlugin);
        pluginModules.add(locationPlugin);
        return pluginModules;

    }

    @Override
    public List<IEmoticonTab> getEmoticonTabs() {
        return super.getEmoticonTabs();
    }
}
