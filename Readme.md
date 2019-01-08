## Basic chart controls

### Chart Types

There are different chart types: 

* bar
* column
* line
* spline
* area
* pie
* doughnut
* radar
* rose
* scatter
* bubble
* heatmap
* timeline
* candle
* meter

 To change the type of your chart, set the **type** property in your json data to one of these values.

The following example is equal to our first chart, but the type was changed to "spline":

    <chart><!--{
      data: { dimensions: [[Mon, Tue, Wed, Thu, Fri, Sat, Sun]], values: [10,20,30,80,70,50,35] },
      type: line, width: 500, height: 250
    }--></chart>
    
Some chart (scatter and bar charts) need data with two or three values per sample and series.

###Titles

Charts can contains titles and / or subtitles. Just use the **title** and **subtitle** attributes of the JSON object to set them:

    <chart><!--{
      data: { dimensions: [[Mon, Tue, Wed, Thu, Fri, Sat, Sun]], values: [10,20,30,80,70,50,35] },
      type: bar, width: 500, height: 250,
      title: Example chart, subtitle: with subtitle
    }--></chart>

You can also change the appearance of the titles with the **titlefont**, **titlecolor**, **subtitlefont** and **subtitlecolor** attributes:

    <chart><!--{
      data: { dimensions: [[Mon, Tue, Wed, Thu, Fri, Sat, Sun]], values: [10,20,30,80,70,50,35] },
      type: bar, width: 500, height: 200,
      title: Example chart, titlefont: "Arial,20", titlecolor: blue,
      subtitle: with subtitle, subtitlefont: "Arial, 8", subtitlecolor: green
    }--></chart>

###Legends

To add a legend to your chart, simply add a **legend** object to you JSON data. Inside the legend object, you can add additional properties to change the position and appearance of the legend.

The **position** and **aligment** attributes allow set the position of the legend. Both can be set to "top", "bottom", "left" or "right" and aligment additionally to "middle". E.g. position: right together with alignment: top displays the legend in the upper right corner.

The **inside** attribute places the legend inside your chart (then it may hide the data of the chart though). This example displays the legend in the lower right corner inside the chart:

    <chart><!--{
      data: { dimensions: [[Mon, Tue, Wed, Thu, Fri, Sat, Sun], [Germany, Denmark]], 
              values: [[10,20,30,80,70,50,35],[21,18,25,55,51,33,28]] },
      type: bar, width: 500, height: 250,
      legend: { position: bottom, alignment: right, inside: true }
    }--></chart>
    
The legend is interactive by default. Move with the mouse over the legend labels and you will see how the sample values appear for the series you hover. Also, by clicking on a series label in the legend, you can let them disappear.

The attributes **border**, **background**, **shadow**, **shadowoffsetx** and **shadowoffsety** control the border, background and shadow of the legend. The **corner** attribute sets the radius of the round corner. E.g., to hide the border of the legend, set its border to "null":

    <chart><!--{
      data: { dimensions: [[Mon, Tue, Wed, Thu, Fri, Sat, Sun], [Germany, Denmark]], 
              values: [[10,20,30,80,70,50,35],[21,18,25,55,51,33,28]] },  type: bar, width: 500, height: 250,
      legend: { background: null, border: null }
    }--></chart>
    
The attributes **font**,** colors**, and **color** change the used font and color(s) for the texts inside the legend:

    <chart><!--{
      data: { dimensions: [[Mon, Tue, Wed, Thu, Fri, Sat, Sun], [Germany, Denmark]], 
              values: [[10,20,30,80,70,50,35],[21,18,25,55,51,33,28]] },
      type: line, width: 500, height: 250,
      legend: { font: "Courier New, bold, 20", color: blue}
    }--></chart>
    
With the attributes **padding** and **labelspacing**, you can control the space around the texts and between the texts:

    <chart><!--{
      data: { dimensions: [[Mon, Tue, Wed, Thu, Fri, Sat, Sun], [Germany, Denmark]], 
              values: [[10,20,30,80,70,50,35],[21,18,25,55,51,33,28]] },
      type: line, width: 500, height: 250,
      legend: { padding: 20, labelspacing: 5}
    }--></chart>
    
Finally, the **labels** attribute enables you to replace the texts in the legend with custom texts and the reverse attribute to reverse the order of the labels:

    <chart><!--{
      data: { dimensions: [[Mon, Tue, Wed, Thu, Fri, Sat, Sun], [Germany, Denmark]], 
              values: [[10,20,30,80,70,50,35],[21,18,25,55,51,33,28]] },
      type: line, width: 500, height: 250,
      legend: { labels: [DE,DK], reverse: true }
    }--></chart>
    
##Axis control

Basically, there are two types of axes in iolapCharts: The SampleAxis and the ScaleAxis. A ScaleAxis is scalar and used for numerical values, typically the Y-Axis in a non rotated chart. The sample axis display the samples of a chart (usually text) and is not scalar. Both types share a lot of attributes but have own attributes, too.

To change the axis settings, add an object attribute with the name **scale**, **scale2**, **samples** or **samples2** to you json definition.

###Shared attributes

First of all, each axis can have a title. The title of an axis is controlled by the **title** property, the properties **color**, **titlefont** and **titleangle** (in degrees) control its appearence.

    <chart><!--{
      data: { dimensions: [[Mon, Tue, Wed, Thu, Fri, Sat, Sun]], values: [10,20,30,80,70,50,35] },
      type: bar, width: 500, height: 250,
      scale: { title: Amount, titleangle: 270 },
      samples: { title: Weekday, font: Courier New, titlecolor: green }
    }--></chart>
    
With **line** and **showbaseline**, you can create lines for the axis, and **line** controls their color. The baseline is the the long vertical or horizontal line whichs separates the scale from the chart itself. And the tickwidth determines the size of the small lines connecting the baseline and the number.

    <chart><!--{
      data: { dimensions: [[Mon, Tue, Wed, Thu, Fri, Sat, Sun]], values: [10,20,30,80,70,50,35] },
      type: bar, width: 500, height: 250,
      scale: { baseline: true, line: red, tickwidth: 5 }
    }--></chart>
    
By default, samples labels are omitted if there is not enough space for them. With **autospacing** you can disable this feature and the labels may overlap.

    <chart><!--{
      data: { dimensions: [[First, Second, Third, Fourth, Sixth, Seventh, Eight, Ninth]],
              values: [10,20,30,80,70,50,35,31,44] }, 
      type: line, width: 500, height: 250, 
      samples: { autospacing: false }
    }--></chart>
    
Better gain some space for your labels by rotating them with **labelangle** or make them smaller with **font**:

    <chart><!--{
      data: { dimensions: [[First, Second, Third, Fourth, Sixth, Seventh, Eight, Ninth]],
              values: [10,20,30,80,70,50,35,31,44] },
      type: line, width: 500, height: 250,
      samples: { labelangle: 270, font: "Arial, 8" }
    }--></chart>
    
Or just disable the labels with **showlabels**:

    <chart><!--{
      data: { dimensions: [[First, Second, Third, Fourth, Sixth, Seventh, Eight, Ninth]],
              values: [10,20,30,80,70,50,35,31,44] },
      type: bar, width: 500, height: 250,
      samples: { showlabels: false }
    }--></chart>
    
Additional texts can be added with **prefix** and **postfix** . This is useful e.g. to add units to your texts:

    <chart><!--{
     data: { dimensions: [[First, Second, Third, Fourth, Sixth, Seventh, Eight, Ninth]],
              values: [10,20,30,80,70,50,35,31,44] },
     type: bar, width: 500, height: 250, 
     scale: { postfix: "€" }
    }--></chart>
    
###Scale axis attributes

With **min** and **max** you can change the upper and lower bound of a scale axis. Without setting these bounds they are calculated automatically on base of the used data.

    <chart><!--{
      data: { dimensions: [[Mon, Tue, Wed, Thu, Fri, Sat, Sun]], values: [-20,-10,20,80,60,50,35] },
      type: bar, width: 500, height: 250,
      scale: { min: -100, max: 100 }
    }--></chart>
    
The format of the numbers is also set automatically. To change it, the number of digits behind the comma can be set with **decimalcount**. Or you may set the whole format of the numbers with the **format** attribute.

    <chart><!--{
      data: { dimensions: [[Mon, Tue, Wed, Thu, Fri, Sat, Sun]], values: [-20.32,-10.50,20.25,80.00,70.21,50.22,35.23] },
      type: column, width: 500, height: 250,
      scale: { format: "#,###,##0.00" }
    }--></chart>
    
The space between two ticks is by default at minimum 25 pixels - this can be changed with the **minticksize** attribute. On the other hand, you may want to define the maximum number of ticks with the **maxlinecount** attribute.

    <chart><!--{
      data: { dimensions: [[Mon, Tue, Wed, Thu, Fri, Sat, Sun]], values: [-20,-10,20,80,60,50,35] },
      type: bar, width: 500, height: 250,
      scale: { minticksize: 10 }
    }--></chart>
    
Scale axes may also contain targets, additional ticks with target values and optional label and color. One or more targets maybe defined as an array in the **targets** attribute, each with a **value**, **text** and **color** attribute.

    <chart><!--{
      data: { dimensions: [[Mon, Tue, Wed, Thu, Fri, Sat, Sun]], values: [-20,-10,20,80,60,50,35] },
      type: bar, width: 500, height: 250,
      scale: { targets: [{ value: 10, text: min, color: green },
                         { value: 90, text: max, color: red }] }
    }--></chart>
    
Or define critical areas in the **areas** attribute. Each area has a **min**, **max**, **text** and **color** attribute:

    <chart><!--{
      data: { dimensions: [[Mon, Tue, Wed, Thu, Fri, Sat, Sun]], values: [-20,-10,20,80,60,50,35] },
      type: bar, width: 500, height: 250,
      scale: { areas: [{ min: 10, max: 90, text: Standard, color: "green,25" }] }
    }--></chart>
    
###Sample axis attributes

In sample axes, you can replace the original labels from the data with your own texts using the **labels** attribute:

    <chart><!--{
      data: { dimensions: [[First, Second, Third, Fourth, Sixth, Seventh, Eight, Ninth]],
              values: [10,20,30,80,70,50,35,31,44] },
      type: bar, width: 500, height: 250,
      samples: { labels: [1st,2nd,3rd,4th,5th,6th,7th,8th,9th,10th] }
    }--></chart>
    
##Canvas control

The canvas is the inner area of a chart where the bars, lines etc. are displayed. In the **canvas** area of your json definition, the appearance of the canvas can be changed and controlled.

The **border**, **corner**, **shadow**, **shadowoffsetx** and **shadowoffsety** attributes add a border and/or shadow to the canvas. The background attribute changes the **background** color of the canvas.

    <chart><!--{
      data: { dimensions: [[Mon, Tue, Wed, Thu, Fri, Sat, Sun]], values: [-20,-10,20,80,60,50,35] },
      type: column, width: 500, height: 250,
      canvas: { background: "#eee", border: black, corner: 20, shadow: "black,33" }
    }--></chart>
    
Instead of changing the complete background color, the attributes **horizontalbackground** and **verticalbackground** only fill every 2nd area (between the ticks) of the grid:
    
    <chart><!--{
      data: { dimensions: [[Mon, Tue, Wed, Thu, Fri, Sat, Sun]], values: [-20,-10,20,80,60,50,35] },
      type: bar, width: 500, height: 250,
      canvas: { horizontalbackground: "#eee" }
    }--></chart>

Gridlines can be controlled with the **horizontalgrid**, **horizontalstroke**, **verticalgrid** and **verticalstroke** attributes:

    <chart><!--{
      data: { dimensions: [[Mon, Tue, Wed, Thu, Fri, Sat, Sun]], values: [-20,-10,20,80,60,50,35] },
      type: bar, width: 500, height: 250,
      canvas: { horizontalgrid: black, verticalgrid: gray, verticalstroke: "2|2" }
    }--></chart>
    
The **baseline** attribute lets the chart the basline of the grid in the defined color (the baseline is the gridline for the value 0 of the scale axis):

    <chart><!--{
      data: { dimensions: [[Mon, Tue, Wed, Thu, Fri, Sat, Sun]], values: [-20,-10,20,80,60,50,35] },
      type: bar, width: 500, height: 250,
      canvas: { baseline: black }
    }--></chart>
    
##Contents

The content of a chart is the the lines, bars, pie slices - or whatever the chart intentionally displays. To control the content, add a content object to your JSON config. The following attributes are available for all kinds of content:

###Shared attributes

The attributes **color** and **colors** change the color of your content. If the attribute color is used, it changes the color for all series to the same value. The attribute colors accepts an array with different colors for each series:

    <chart><!--{
      data: { dimensions: [[Mon, Tue, Wed, Thu, Fri, Sat, Sun], [Germany, Denmark]],
              values: [[10,20,30,80,70,50,35],[21,18,25,55,51,33,28]] },
      type: line, width: 500, height: 250,
      content: { colors: [red, blue] }
    }--></chart>
    
The **shadow**, **shadowxoffset** and **shadowyoffset** attributes add a shadow to your content element. By default, a light shadow is set for all types of contents. To disable it, set the shadow attribute to null:

    <chart><!--{
      data: { dimensions: [[Mon, Tue, Wed, Thu, Fri, Sat, Sun], [Germany, Denmark]],
              values: [[10,20,30,80,70,50,35],[21,18,25,55,51,33,28]] },
      type: line, width: 500, height: 250,
      content: { shadow: null }
    }--></chart>
    
The boolean attributes **valuelabels**, **samplelabels** and **serieslabels** let the names of the series, samples or the value appear permanentley in the content. With **labelspacing**, the distance to the content can be changed. And the attributes **labelfont**, **labelcolor** and **labelangle** can be used to change their design:

    <chart><!--{
      data: { dimensions: [[Mon, Tue, Wed, Thu, Fri, Sat, Sun]], values: [10,20,30,80,70,50,35] },
      type: pie, width: 500, height: 250,
      content: { valuelabels: true, samplelabels: true, labelangle: 270, labelcolor: red }
    }--></chart>
    
Like the labels, the popup texts can be enabled or disabled with **valuepopup**, **samplepopup** and **seriespopup**. The attributes **popupfont** changes the font used in the popups:

    <chart><!--{
      data: { dimensions: [[Mon, Tue, Wed, Thu, Fri, Sat, Sun]], values: [10,20,30,80,70,50,35] },
      type: pie, width: 500, height: 250,
      content: { samplepopup: false, seriespopup: false, popupfont: Courier New }
    }--></chart>
    
To add a text befor or behind the valuelabels or valuepopups, use the attributes **prefix** and **postfix**, e.g. to add the unit to unit:

    <chart><!--{
      data: { dimensions: [[Mon, Tue, Wed, Thu, Fri, Sat, Sun]], values: [10,20,30,80,70,50,35] },
      type: bar, width: 500, height: 250,
      content: { valuelabels: true, prefix: "Amount: ", postfix: "€" }
    }--></chart>
    
Most content types support outlines for their elements, like bars, pie slices or lines. By default, alle content types have outlines - with the **outline** property you can change or disable them (with null).

    <chart><!--{
      data: { dimensions: [[Mon, Tue, Wed, Thu, Fri, Sat, Sun]], values: [10,20,30,80,70,50,35] },
      type: bar, width: 500, height: 250,
      content: { outline: null }
    }--></chart>
    
Also, most content types are able to add some luminance effects to their element, and for pie slices this is enabled by default. The **shine** attribute allows to control it in two different ways. If the value is larger than 1, it's the shine size in pixels. Otherwise the percentage of the bars or slices (e.g. 0.5 means half of a bar becomes brighter and the other half darker).

    <chart><!--{
      data: { dimensions: [[Mon, Tue, Wed, Thu, Fri, Sat, Sun]], values: [10,20,30,80,70,50,35] },
      type: column, width: 500, height: 250,
      content: { shine: 5 }
    }--></chart>
    
Some content types are able to add a automatic regression line  to their content, e.g. bar, line, scatter or bubble charts. By default, this line is disabled, but with the attributes **regression** and **regressionstroke** you can set its color a stroke:

    <chart><!--{
      data: { dimensions: [[Mon, Tue, Wed, Thu, Fri, Sat, Sun]], values: [-20,-10,20,80,60,50,35] },
      type: bar, width: 500, height: 250,
      content: { regression: black, regressionstroke: "2" }
    }--></chart>
    
###Bar, Column & Rose attributes

The size of the bars is defined by the **barwidth** attribute. It accepts values between 0 and 1 - 1 means it uses the whole available space, 0.5 only half of it and so on.

    <chart><!--{
      data: { dimensions: [[Mon, Tue, Wed, Thu, Fri, Sat, Sun]], values: [-20,-10,20,80,60,50,35] },
      type: rose, width: 500, height: 250,
      content: { barwidth: 0.25 }
    }--></chart>
    
For data with multiple series, the attribute **barspacing** determines the space between the different bars of a sample. It accepts values between 0 and 1 whereas 0 means, there is no space between the bars and 1 the maximum possible space is used.

    <chart><!--{
      data: { dimensions: [[Mon, Tue, Wed, Thu, Fri, Sat, Sun], [Germany, Denmark]],
              values: [[10,20,30,80,70,50,35],[21,18,25,55,51,33,28]] },
      type: bar, width: 500, height: 250,
      content: { barwidth: 1, barspacing: 0.5 }
    }--></chart>
    
By default, Bar Charts with a single series use the same color for all bars. If want some more color in your chart, use the boolean attribute **multicolor** to use different colors for each bar:

    <chart><!--{
      data: { dimensions: [[Mon, Tue, Wed, Thu, Fri, Sat, Sun]], values: [10,20,30,80,70,50,35] },
      type: column, width: 500, height: 250,
      content: { multicolor: true }
    }--></chart>
    
###Line, Spline & Area attributes

Missing data points are connected in Line Charts by default. With the boolean **connected** attribute it's possible to disable this feature:

    <chart><!--{
      data: { dimensions: [[Mon, Tue, Wed, Thu, Fri, Sat, Sun]], values: [10,20,30,null,70,50,35] },
      type: line, width: 500, height: 250,
      content: { connected: false }
    }--></chart>
    
In area charts, the same color is used for the area as for the line itself, but with less opacity (50%). The **areaopacity** attribute enables you to change it:

    <chart><!--{
      data: { dimensions: [[Mon, Tue, Wed, Thu, Fri, Sat, Sun]], values: [10,20,30,80,70,50,35] },
      type: area, width: 500, height: 250,
      content: { areaopacity: 1 }
    }--></chart>
    
###Pie & Doughnut attributes

Pie & Doughnut Charts are able to detach their slices in order to attrack the viewers' attention. The attribute **detached** controls the ordinals of the deteached slices and the attribute **distance** controls the distance as a value between 0 and 1. By default, no slices are detached and the standard distance is 0.2:

    <chart><!--{
      data: { dimensions: [[Mon, Tue, Wed, Thu, Fri, Sat, Sun]], values: [10,150,20,80,20,40,35] },
      type: pie, width: 500, height: 250,
      content: { detached: [3,4], detachdistance: 0.33 }
    }--></chart>
    
With the **startangle** attribute it is possible to set the position of the 1st slice. By default it's 0 degrees (like 12 o'clock). E.g. this example changes it to 90 degrees:

    <chart><!--{
      data: { dimensions: [[Mon, Tue, Wed, Thu, Fri, Sat, Sun]], values: [10,150,20,80,20,40,35] },
      type: pie, width: 500, height: 250,
      content: { startangle: 90 }
    }--></chart>
    
Doughnut Charts are just Pie Charts with a space before the 1st series and between two serieses. Use the attribute **seriesspace** to change it, it accepts value between 0 (no space) and 1 (maximum space):

    <chart><!--{
      data: { dimensions: [[Mon, Tue, Wed, Thu, Fri, Sat, Sun]], values: [10,150,20,80,20,40,35] },
      type: pie, width: 500, height: 250,
      content: { seriesspace: 0.8 }
    }--></chart>

# Data transformation

In the last tutorials, we displayed the data in our charts "as is" without any modification. But OLAPCharts is also able to transform the data before it is used in charts, e.g. you may want to aggregate your data or extrace two dimensions out of a multidimensional cube.

To demonstrate the transformations, we'll define a cube with 2 dimensions first.

    <cube name="cube1"><!--{
      dimensions: [
        [Mon, Tue, Wed, Thu, Fri, Sat, Sun],
        [Product 1, Product 2, Product 3]
      ], 
      values: [
        [10,20,10,-10,-15,-5,15],
        [5,10,15,20,25,30,35],
        [1,2,3,4,5,6,7]
      ]
    }--></cube>

This is the data original data as a bar chart:

    <chart datasource="cube1"><!--{
      type:bar, width: 450, height: 250
    }--></chart>

The you can add transformations to the data, either in the cube itself, another cube or in the chart. Transformations are defined with the <strong>transform</strong> attribute of the cube or charts. It expects an array of object and each object must at least contain the <strong>type</strong> attribute (which defines the type of transformation you want  to perform):

    <chart datasource="cube1"><!--{
      transform: [{ type: accumulate }],
      type:bar, width: 450, height: 250
    }--></chart>

This example takes the original and performs an accumulate transformation on this.
