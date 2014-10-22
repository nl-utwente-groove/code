<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph role="graph" edgeids="false" edgemode="directed" id="start-2">
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
        <node id="n239">
            <attr name="layout">
                <string>73 151 27 36</string>
            </attr>
        </node>
        <node id="n238">
            <attr name="layout">
                <string>176 193 27 36</string>
            </attr>
        </node>
        <node id="n237">
            <attr name="layout">
                <string>174 113 27 36</string>
            </attr>
        </node>
        <node id="n240">
            <attr name="layout">
                <string>281 156 27 36</string>
            </attr>
        </node>
        <edge from="n239" to="n239">
            <attr name="label">
                <string>type:Phil</string>
            </attr>
        </edge>
        <edge from="n239" to="n239">
            <attr name="label">
                <string>flag:think</string>
            </attr>
        </edge>
        <edge from="n239" to="n237">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge from="n239" to="n238">
            <attr name="label">
                <string>right</string>
            </attr>
        </edge>
        <edge from="n238" to="n238">
            <attr name="label">
                <string>type:Fork</string>
            </attr>
        </edge>
        <edge from="n238" to="n238">
            <attr name="label">
                <string>flag:free</string>
            </attr>
        </edge>
        <edge from="n237" to="n237">
            <attr name="label">
                <string>type:Fork</string>
            </attr>
        </edge>
        <edge from="n237" to="n237">
            <attr name="label">
                <string>flag:free</string>
            </attr>
        </edge>
        <edge from="n240" to="n240">
            <attr name="label">
                <string>type:Phil</string>
            </attr>
        </edge>
        <edge from="n240" to="n240">
            <attr name="label">
                <string>flag:think</string>
            </attr>
        </edge>
        <edge from="n240" to="n237">
            <attr name="label">
                <string>right</string>
            </attr>
        </edge>
        <edge from="n240" to="n238">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
    </graph>
</gxl>
