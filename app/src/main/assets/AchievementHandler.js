let achievementInProgress = false;
const achievementQueue = [];
const achievementQueueMAX = [];
let volume = 1
class AchievementShower extends Entity {
    constructor(options = false) {
        let settings = {parent:camera.ui,title:'',scale:[.4,.25],xy:[0.688,-.63],icon:'',roundness:.1,color:color.black}
    for (const [key, value] of Object.entries(options)) {
        settings[key] = value
    }
    super(settings)
    this.iconShower = new Entity({parent:this,texture:this.icon,scale:[.5,.5]})
    this.animate('y', -.4, 1.5)
    this.soundfx = new Audio('assets/rising.ogg')
    this.soundfx.play()
    this.soundfx.volume = volume
    this.texts = new Text({text:'ACHIEVEMENT UNLOCKED!',text_size:2.725,x:-.1,y:-.05,parent:this,text_color:color.green})
    this.title = new Text({text:this.title,parent:this,text_size:3,x:-.1,y:-.75,parent:this,text_color:color.white})
}
    
    static showNext() {
        if (!achievementInProgress && achievementQueue.length > 0) {
            achievementInProgress = true;
            const achievementOptions = achievementQueue.shift();
            const achievement = new AchievementShower(achievementOptions);
            achievement.onComplete(() => {
                achievementInProgress = false;
                AchievementShower.showNext();
            });
        }
    }
     
    onComplete(callback) {
        after(3, () => {
            this.animate('y', -.63, 1.5)
            after(1.5, () => {destroy(this);
            if (typeof callback === 'function') {
                callback();
            }})
            
        });
    } 
}
class AchievementShowerMAX extends Entity {
    constructor(options = false) {
        let settings = {parent:camera.ui,title:'',scale:[.7,.1],xy:[1.25,.33],icon:'',roundness:.1,color:hsv(0,0,.15)}
    for (const [key, value] of Object.entries(options)) {
        settings[key] = value
    }
    super(settings)
    this.animate('x', .55, 1.25)
    this.soundfx = new Audio('assets/UltraLevelUp.mp3')
    this.soundfx.play()
    this.soundfx.volume = volume
    this.iconShower = new Entity({parent:this,texture:this.icon,scale:[.25,.9],xy:[.3,0]})
    this.texts = new Text({text:'ACHIEVEMENT UNLOCKED!',text_size:2.725,x:-.1,y:-.05,parent:this,text_color:color.green})
    this.title = new Text({text:this.title,parent:this,text_size:3,x:-.1,y:-.5,parent:this,text_color:color.white})
}
    
    static showNext() {
        if (!achievementInProgress && achievementQueueMAX.length > 0) {
            achievementInProgress = true;
            const achievementOptions = achievementQueueMAX.shift();
            const achievement = new AchievementShowerMAX(achievementOptions);
            achievement.onComplete(() => {
                achievementInProgress = false;
                AchievementShowerMAX.showNext();
            });
        }
    }
      
    onComplete(callback) {
        after(3.5, () => {
            this.animate('x', 1.25 , 1.5)
            after(1.7, () => {destroy(this);
            if (typeof callback === 'function') {
                callback();
            }})
            
        });
    } 
}

clicked1achievement = save_system_load('clickedonceachievement') || 0;
clicked5000achievement = save_system_load('clicked5000achievement') || 0;
clicked500000achievement = save_system_load('clicked500000achievement') || 0;

let spent1godlyclick = save_system_load('spent1godlyclicks') || 0;
let spent10godlyclick = save_system_load('spent10godlyclicks') || 0;
let spent50godlyclick = save_system_load('spent50godlyclicks') || 0;

AchievementHandler = new Entity({ alpha: 0 });
AchievementHandler.update = function () {
    if (score >= 1 && !clicked1achievement) {
        clicked1achievement = 1;
        save_system_save('clickedonceachievement', clicked1achievement);
        achievementQueue.push({ alpha: 1, icon: 'Clicker-idle.gif', title:'First click!' });
    } 
    if (score >= 5000 && !clicked5000achievement) {
        clicked5000achievement = 1;
        save_system_save('clicked5000achievement', clicked5000achievement);
        achievementQueue.push({ alpha: 1, icon: 'clickerManLevel2.webp', title:'5000 clicks!' });
    } 
    if (score >= 500000 && !clicked500000achievement) {
        clicked500000achievement = 1;
        save_system_save('clicked500000achievement', clicked500000achievement);
        achievementQueueMAX.push({alpha: 1, icon: 'clickerManLevel3.webp', title:'Reached 500k clicks!' });
    } 
    if (godlyClicksTracker >= 1 && !spent1godlyclick){
        spent1godlyclick = 1;
        save_system_save('spent1godlyclicks', spent1godlyclick);
        achievementQueue.push({ alpha: 1, icon: 'GodlyClick.webp', title:'First godly click!' });
    }
    if (godlyClicksTracker >= 10 && !spent10godlyclick){
        spent10godlyclick = 1;
        save_system_save('spent10godlyclicks', spent10godlyclick);
        achievementQueue.push({ alpha: 1, icon: 'GodlyClick.webp', title:'Spent 10 godly clicks!' });
    }
    if (godlyClicksTracker >= 50 && !spent50godlyclick){
        spent50godlyclick = 1;
        save_system_save('spent50godlyclicks', spent50godlyclick);
        achievementQueueMAX.push({ alpha: 1, icon: 'GodlyClick.webp', title:'Spent 50 godly clicks!' });
    }


    AchievementShower.showNext();
    AchievementShowerMAX.showNext();
};