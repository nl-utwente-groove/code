<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start-with-final">
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
        <node id="n2">
            <attr name="layout">
                <string>327 110 7 15</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>155 260 4 15</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>140 104 40 15</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>441 110 19 19</string>
            </attr>
        </node>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>p</string>
            </attr>
        </edge>
        <edge to="n1" from="n2">
            <attr name="label">
                <string>q</string>
            </attr>
            <attr name="layout">
                <string>503 -3 319 128 169 258 11</string>
            </attr>
        </edge>
        <edge to="n3" from="n2">
            <attr name="label">
                <string>p</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>r</string>
            </attr>
        </edge>
        <edge to="n0" from="n1">
            <attr name="label">
                <string>q</string>
            </attr>
            <attr name="layout">
                <string>500 0 157 268 160 112 12</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>flag:current</string>
            </attr>
        </edge>
        <edge to="n1" from="n0">
            <attr name="label">
                <string>p</string>
            </attr>
        </edge>
        <edge to="n2" from="n0">
            <attr name="label">
                <string>p</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string></string>
            </attr>
        </edge>
    </graph>
</gxl>
