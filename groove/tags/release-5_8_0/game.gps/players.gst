<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="players">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n8">
            <attr name="layout">
                <string>326 69 80 15</string>
            </attr>
        </node>
        <node id="n7">
            <attr name="layout">
                <string>328 171 82 15</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>129 50 37 45</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>132 159 37 30</string>
            </attr>
        </node>
        <edge to="n8" from="n8">
            <attr name="label">
                <string>color:255,0,0</string>
            </attr>
        </edge>
        <edge to="n8" from="n8">
            <attr name="label">
                <string>id:startRed</string>
            </attr>
        </edge>
        <edge to="n8" from="n8">
            <attr name="label">
                <string>type:Pos</string>
            </attr>
        </edge>
        <edge to="n7" from="n7">
            <attr name="label">
                <string>color:0,0,255</string>
            </attr>
        </edge>
        <edge to="n7" from="n7">
            <attr name="label">
                <string>id:startBlue</string>
            </attr>
        </edge>
        <edge to="n7" from="n7">
            <attr name="label">
                <string>type:Pos</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>color:255,0,0</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:Player</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>flag:red</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>flag:turn</string>
            </attr>
        </edge>
        <edge to="n8" from="n1">
            <attr name="label">
                <string>on</string>
            </attr>
        </edge>
        <edge to="n8" from="n1">
            <attr name="label">
                <string>owns</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>color:0,0,255</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>type:Player</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>flag:blue</string>
            </attr>
        </edge>
        <edge to="n7" from="n2">
            <attr name="label">
                <string>on</string>
            </attr>
        </edge>
        <edge to="n7" from="n2">
            <attr name="label">
                <string>owns</string>
            </attr>
        </edge>
    </graph>
</gxl>
