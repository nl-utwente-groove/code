<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start">
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
        <node id="n4">
            <attr name="layout">
                <string>241 123 35 30</string>
            </attr>
        </node>
        <node id="n5">
            <attr name="layout">
                <string>12 121 35 30</string>
            </attr>
        </node>
        <node id="n8">
            <attr name="layout">
                <string>133 207 35 30</string>
            </attr>
        </node>
        <node id="n6">
            <attr name="layout">
                <string>142 26 21 15</string>
            </attr>
        </node>
        <node id="n7">
            <attr name="layout">
                <string>137 129 38 15</string>
            </attr>
        </node>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>type:Cell</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>flag:empty</string>
            </attr>
        </edge>
        <edge to="n8" from="n4">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n5" from="n5">
            <attr name="label">
                <string>type:Cell</string>
            </attr>
        </edge>
        <edge to="n5" from="n5">
            <attr name="label">
                <string>flag:empty</string>
            </attr>
        </edge>
        <edge to="n6" from="n5">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n8" from="n8">
            <attr name="label">
                <string>type:Cell</string>
            </attr>
        </edge>
        <edge to="n8" from="n8">
            <attr name="label">
                <string>flag:empty</string>
            </attr>
        </edge>
        <edge to="n5" from="n8">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n6" from="n6">
            <attr name="label">
                <string>type:Cell</string>
            </attr>
        </edge>
        <edge to="n6" from="n6">
            <attr name="label">
                <string>flag:empty</string>
            </attr>
        </edge>
        <edge to="n4" from="n6">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n7" from="n7">
            <attr name="label">
                <string>type:Buffer</string>
            </attr>
        </edge>
        <edge to="n6" from="n7">
            <attr name="label">
                <string>first</string>
            </attr>
        </edge>
        <edge to="n5" from="n7">
            <attr name="label">
                <string>last</string>
            </attr>
        </edge>
    </graph>
</gxl>
