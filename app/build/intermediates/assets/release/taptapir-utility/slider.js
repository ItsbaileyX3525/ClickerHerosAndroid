class Slider extends Entity{
    constructor(options) {
        let settings = {parent: camera.ui,scale: [0.45, 0.05],value: 10,x:0,y: 0,background_color: color.white,button_color: color.blue};
        for (const [key, value] of Object.entries(options)) {
            if (value !== false) {
                settings[key] = value;
            }
        }
        
        super(settings);

        this.alpha = 0;
        this.sliderBackground = new Entity({scale: [settings.scale[0] + settings.scale[1], settings.scale[1]],x: settings.x,y: settings.y,roundness: 0.5,color: settings.background_color});
        this.sliderVal = new Entity({scale: [settings.scale[0], 0],x: settings.x,y: settings.y});
        this.sliderButton = new Button({scale: [settings.scale[1]*1.6, settings.scale[1]*1.5],x: settings.x,y: settings.y+0.0005,roundness: 0.5,color: settings.button_color,draggable: true,lock_y: true});
        this.value = settings.value;
        
        this.sliderButton.x = (this.sliderButton.x = this.value * this.sliderVal.scale_x / 100 - this.sliderVal.scale_x / 2);
        }
    update() {
        if (this.sliderButton.x <= (-this.sliderVal.scale_x/2+0)) {
            this.sliderButton.x = -this.sliderVal.scale_x/2+0;
        }
        if (this.sliderButton.x >= (this.sliderVal.scale_x/2-0)) {
            this.sliderButton.x = this.sliderVal.scale_x/2-0;
        }
        this.value = round((this.sliderButton.x + this.sliderVal.scale_x/2) * 100 / this.sliderVal.scale_x, 0);
    }
    setValue = function(value=100) {
        this.sliderButton.x = (this.sliderButton.x = value * this.sliderVal.scale_x / 100 - this.sliderVal.scale_x / 2);
    }
}