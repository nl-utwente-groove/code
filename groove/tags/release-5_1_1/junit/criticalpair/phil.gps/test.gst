<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph role="graph" edgeids="false" edgemode="directed" id="test">
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
        <node id="n0">
            <attr name="layout">
                <string>168 206 24 36</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>138 90 27 18</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>256 112 27 18</string>
            </attr>
        </node>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>type:Phil</string>
            </attr>
        </edge>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>flag:eat</string>
            </attr>
        </edge>
        <edge from="n0" to="n1">
            <attr name="label">
                <string>hold</string>
            </attr>
            <attr name="layout">
                <string>500 0 173 212 141 153 169 111 11</string>
            </attr>
        </edge>
        <edge from="n0" to="n1">
            <attr name="label">
                <string>left</string>
            </attr>
            <attr name="layout">
                <string>500 0 173 212 141 153 169 111 11</string>
            </attr>
        </edge>
        <edge from="n0" to="n2">
            <attr name="label">
                <string>hold</string>
            </attr>
        </edge>
        <edge from="n0" to="n2">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge from="n1" to="n1">
            <attr name="label">
                <string>type:Fork</string>
            </attr>
        </edge>
        <edge from="n2" to="n2">
            <attr name="label">
                <string>type:Fork</string>
            </attr>
        </edge>
    </graph>
</gxl>
