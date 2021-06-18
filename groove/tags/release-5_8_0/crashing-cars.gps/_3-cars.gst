<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="_3-cars">
        <attr name="transitionLabel">
            <string></string>
        </attr>
        <attr name="enabled">
            <string>true</string>
        </attr>
        <attr name="priority">
            <string>0</string>
        </attr>
        <attr name="printFormat">
            <string></string>
        </attr>
        <attr name="remark">
            <string></string>
        </attr>
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n7">
            <attr name="layout">
                <string>391 88 17 15</string>
            </attr>
        </node>
        <node id="n5">
            <attr name="layout">
                <string>143 29 27 15</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>146 87 17 15</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>310 87 17 15</string>
            </attr>
        </node>
        <node id="n6">
            <attr name="layout">
                <string>308 29 29 15</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>58 86 17 15</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>59 29 27 15</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>232 79 22 30</string>
            </attr>
        </node>
        <edge to="n7" from="n7">
            <attr name="label">
                <string>type:RS</string>
            </attr>
        </edge>
        <edge to="n7" from="n7">
            <attr name="label">
                <string>flag:free</string>
            </attr>
        </edge>
        <edge to="n1" from="n7">
            <attr name="label">
                <string>l</string>
            </attr>
            <attr name="layout">
                <string>759 8 400 95 319 94 12</string>
            </attr>
        </edge>
        <edge to="n5" from="n5">
            <attr name="label">
                <string>type:LCar</string>
            </attr>
        </edge>
        <edge to="n2" from="n5">
            <attr name="label">
                <string>at</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>type:RS</string>
            </attr>
        </edge>
        <edge to="n3" from="n2">
            <attr name="label">
                <string>r</string>
            </attr>
        </edge>
        <edge to="n4" from="n2">
            <attr name="label">
                <string>l</string>
            </attr>
            <attr name="layout">
                <string>803 6 155 94 67 93 12</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:RS</string>
            </attr>
        </edge>
        <edge to="n3" from="n1">
            <attr name="label">
                <string>l</string>
            </attr>
            <attr name="layout">
                <string>787 5 319 94 243 94 12</string>
            </attr>
        </edge>
        <edge to="n7" from="n1">
            <attr name="label">
                <string>r</string>
            </attr>
        </edge>
        <edge to="n6" from="n6">
            <attr name="label">
                <string>type:RCar</string>
            </attr>
        </edge>
        <edge to="n1" from="n6">
            <attr name="label">
                <string>at</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>type:RS</string>
            </attr>
        </edge>
        <edge to="n2" from="n4">
            <attr name="label">
                <string>r</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:LCar</string>
            </attr>
        </edge>
        <edge to="n4" from="n0">
            <attr name="label">
                <string>at</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>type:RS</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>flag:free</string>
            </attr>
        </edge>
        <edge to="n1" from="n3">
            <attr name="label">
                <string>r</string>
            </attr>
        </edge>
        <edge to="n2" from="n3">
            <attr name="label">
                <string>l</string>
            </attr>
            <attr name="layout">
                <string>853 7 243 94 155 94 12</string>
            </attr>
        </edge>
    </graph>
</gxl>
